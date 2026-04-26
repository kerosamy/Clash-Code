package com.clashcode.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.clashcode.backend.exception.FileStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageFileStorageServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private ImageFileStorageService imageFileStorageService;

    @BeforeEach
    void setUp() {
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void storeFile_Success_ReturnsSecureUrl() throws IOException {
        // Arrange
        String username = "testuser";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                "image/jpeg",
                "image content".getBytes()
        );

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://res.cloudinary.com/demo/image/upload/v123/profile-images/testuser_123.jpg");

        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);

        // Act
        String result = imageFileStorageService.storeFile(file, username);

        // Assert
        assertNotNull(result);
        assertEquals("https://res.cloudinary.com/demo/image/upload/v123/profile-images/testuser_123.jpg", result);
        verify(uploader).upload(eq(file.getBytes()), argThat(params -> {
            Map<?, ?> map = (Map<?, ?>) params;
            return map.get("resource_type").equals("image") &&
                    map.get("overwrite").equals(true) &&
                    map.get("public_id").toString().contains("profile-images/testuser");
        }));
    }


    @Test
    void storeFile_VariousImageTypes_Success() throws IOException {
        // Arrange
        String[] imageTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://res.cloudinary.com/demo/image/upload/v123/test.jpg");

        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);

        for (String contentType : imageTypes) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test." + contentType.split("/")[1],
                    contentType,
                    "image content".getBytes()
            );

            // Act
            String result = imageFileStorageService.storeFile(file, "testuser");

            // Assert
            assertNotNull(result);
            assertEquals("https://res.cloudinary.com/demo/image/upload/v123/test.jpg", result);
        }

        verify(uploader, times(4)).upload(any(byte[].class), anyMap());
    }

    @Test
    void storeFile_IOExceptionDuringUpload_ThrowsFileStorageException() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                "image/jpeg",
                "image content".getBytes()
        );

        when(uploader.upload(any(byte[].class), anyMap()))
                .thenThrow(new IOException("Network error"));

        // Act & Assert
        FileStorageException exception = assertThrows(FileStorageException.class,
                () -> imageFileStorageService.storeFile(file, "testuser"));

        assertEquals("Could not upload file to Cloudinary", exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof IOException);
    }

    @Test
    void storeFile_PublicIdContainsUsernameAndTimestamp() throws IOException {
        // Arrange
        String username = "johndoe";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                "image/jpeg",
                "image content".getBytes()
        );

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/test.jpg");

        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);

        // Act
        imageFileStorageService.storeFile(file, username);

        // Assert
        verify(uploader).upload(any(byte[].class), argThat(params -> {
            Map<?, ?> map = (Map<?, ?>) params;
            String publicId = map.get("public_id").toString();
            return publicId.startsWith("profile-images/" + username + "_") &&
                    publicId.contains("_");
        }));
    }

    @Test
    void deleteFile_WithCloudinaryUrl_ExtractsPublicIdAndDeletes() throws IOException {
        // Arrange
        String cloudinaryUrl = "https://res.cloudinary.com/demo/image/upload/v1234567890/profile-images/testuser_123.jpg";

        // Act
        imageFileStorageService.deleteFile(cloudinaryUrl);

        // Assert
        verify(uploader).destroy(eq("profile-images/testuser_123"), argThat(params -> {
            Map<?, ?> map = (Map<?, ?>) params;
            return map.get("resource_type").equals("image");
        }));
    }

    @Test
    void deleteFile_WithPublicId_DeletesDirectly() throws IOException {
        // Arrange
        String publicId = "profile-images/testuser_123";

        // Act
        imageFileStorageService.deleteFile(publicId);

        // Assert
        verify(uploader).destroy(eq(publicId), argThat(params -> {
            Map<?, ?> map = (Map<?, ?>) params;
            return map.get("resource_type").equals("image");
        }));
    }




    @Test
    void deleteFile_IOExceptionDuringDelete_ThrowsFileStorageException() throws IOException {
        // Arrange
        String publicId = "profile-images/testuser_123";

        when(uploader.destroy(anyString(), anyMap()))
                .thenThrow(new IOException("Network error"));

        // Act & Assert
        FileStorageException exception = assertThrows(FileStorageException.class,
                () -> imageFileStorageService.deleteFile(publicId));

        assertEquals("Could not delete file from Cloudinary", exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof IOException);
    }

    @Test
    void deleteFile_VariousUrlFormats_ExtractsCorrectPublicId() throws IOException {
        // Arrange
        String[] testCases = {
                "https://res.cloudinary.com/demo/image/upload/v1234567890/profile-images/user_abc.jpg",
                "https://res.cloudinary.com/mycloud/image/upload/v9999999999/folder/subfolder/image.png",
                "https://res.cloudinary.com/test/image/upload/v1111111111/simple.gif"
        };

        String[] expectedPublicIds = {
                "profile-images/user_abc",
                "folder/subfolder/image",
                "simple"
        };

        // Act & Assert
        for (int i = 0; i < testCases.length; i++) {
            imageFileStorageService.deleteFile(testCases[i]);

            final int index = i;
            verify(uploader).destroy(eq(expectedPublicIds[index]), anyMap());
        }
    }

    @Test
    void deleteFile_UrlWithDifferentExtensions_RemovesExtension() throws IOException {
        // Arrange
        String[] urls = {
                "https://res.cloudinary.com/demo/image/upload/v123/test.jpg",
                "https://res.cloudinary.com/demo/image/upload/v123/test.png",
                "https://res.cloudinary.com/demo/image/upload/v123/test.gif",
                "https://res.cloudinary.com/demo/image/upload/v123/test.webp"
        };

        // Act
        for (String url : urls) {
            imageFileStorageService.deleteFile(url);
        }

        // Assert
        verify(uploader, times(4)).destroy(eq("test"), anyMap());
    }


    @Test
    void storeFile_OverwriteIsTrue() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                "image/jpeg",
                "image content".getBytes()
        );

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/test.jpg");

        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);

        // Act
        imageFileStorageService.storeFile(file, "testuser");

        // Assert
        verify(uploader).upload(any(byte[].class), argThat(params -> {
            Map<?, ?> map = (Map<?, ?>) params;
            return Boolean.TRUE.equals(map.get("overwrite"));
        }));
    }
}