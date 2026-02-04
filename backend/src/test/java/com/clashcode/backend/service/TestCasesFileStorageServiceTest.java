package com.clashcode.backend.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class TestCasesFileStorageServiceTest {

    private TestCasesFileStorageService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new TestCasesFileStorageService();
        ReflectionTestUtils.setField(service, "basePath", tempDir.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up any remaining files
        if (Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            if (!path.equals(tempDir)) {
                                Files.deleteIfExists(path);
                            }
                        } catch (IOException e) {
                            // Ignore
                        }
                    });
        }
    }

    @Test
    void storeTestCase_Success_CreatesFileAndReturnsPath() throws IOException {
        // Arrange
        Long problemId = 1L;
        Long testCaseId = 10L;
        String content = "test input data";
        MultipartFile file = new MockMultipartFile(
                "file",
                "testcase.txt",
                "text/plain",
                content.getBytes()
        );

        // Act
        String result = service.storeTestCase(file, problemId, testCaseId);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("testcase_10_input.txt"));

        Path storedFile = Paths.get(result);
        assertTrue(Files.exists(storedFile));
        assertEquals(content, Files.readString(storedFile));
    }

    @Test
    void storeTestCase_CreatesDirectoryIfNotExists() throws IOException {
        // Arrange
        Long problemId = 999L;
        Long testCaseId = 1L;
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content".getBytes()
        );

        Path problemDir = tempDir.resolve(String.valueOf(problemId));
        assertFalse(Files.exists(problemDir));

        // Act
        String result = service.storeTestCase(file, problemId, testCaseId);

        // Assert
        assertNotNull(result);
        assertTrue(Files.exists(problemDir));
    }


    @Test
    void storeTestCase_MultipleTestCases_CreatesMultipleFiles() throws IOException {
        // Arrange
        Long problemId = 5L;
        MultipartFile file1 = new MockMultipartFile("file1", "test1.txt", "text/plain", "input1".getBytes());
        MultipartFile file2 = new MockMultipartFile("file2", "test2.txt", "text/plain", "input2".getBytes());

        // Act
        String path1 = service.storeTestCase(file1, problemId, 1L);
        String path2 = service.storeTestCase(file2, problemId, 2L);

        // Assert
        assertNotNull(path1);
        assertNotNull(path2);
        assertNotEquals(path1, path2);
        assertTrue(Files.exists(Paths.get(path1)));
        assertTrue(Files.exists(Paths.get(path2)));
    }

    @Test
    void getTestCaseContent_ValidPath_ReturnsContent() throws IOException {
        // Arrange
        Long problemId = 2L;
        Long testCaseId = 5L;
        String expectedContent = "expected test input";

        Path problemDir = tempDir.resolve(String.valueOf(problemId));
        Files.createDirectories(problemDir);

        Path testFile = problemDir.resolve("testcase_5_input.txt");
        Files.writeString(testFile, expectedContent);

        // Act
        String result = service.getTestCaseContent(testFile.toString());

        // Assert
        assertEquals(expectedContent, result);
    }

    @Test
    void getTestCaseContent_NonExistentPath_ReturnsNull() {
        // Arrange
        String nonExistentPath = tempDir.resolve("999/testcase_999_input.txt").toString();

        // Act
        String result = service.getTestCaseContent(nonExistentPath);

        // Assert
        assertNull(result);
    }

    @Test
    void getTestCaseContent_NullPath_ReturnsNull() {
        // Act
        String result = service.getTestCaseContent(null);

        // Assert
        assertNull(result);
    }

    @Test
    void getTestCaseContent_BlankPath_ReturnsNull() {
        // Act
        String result1 = service.getTestCaseContent("");
        String result2 = service.getTestCaseContent("   ");

        // Assert
        assertNull(result1);
        assertNull(result2);
    }

    @Test
    void getTestCaseContent_PathWithWhitespace_TrimsAndReads() throws IOException {
        // Arrange
        Long problemId = 3L;
        String content = "test content";

        Path problemDir = tempDir.resolve(String.valueOf(problemId));
        Files.createDirectories(problemDir);

        Path testFile = problemDir.resolve("testcase_1_input.txt");
        Files.writeString(testFile, content);

        String pathWithWhitespace = "  " + testFile.toString() + "  ";

        // Act
        String result = service.getTestCaseContent(pathWithWhitespace);

        // Assert
        assertEquals(content, result);
    }

    @Test
    void storeTestCaseOutput_Success_CreatesFileAndReturnsPath() throws IOException {
        // Arrange
        Long problemId = 4L;
        Long testCaseId = 15L;
        String outputContent = "expected output";

        // Act
        String result = service.storeTestCaseOutput(outputContent, problemId, testCaseId);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("testcase_15_output.txt"));

        Path storedFile = Paths.get(result);
        assertTrue(Files.exists(storedFile));
        assertEquals(outputContent, Files.readString(storedFile));
    }

    @Test
    void storeTestCaseOutput_CreatesDirectoryIfNotExists() throws IOException {
        // Arrange
        Long problemId = 888L;
        Long testCaseId = 1L;
        String content = "output data";

        Path problemDir = tempDir.resolve(String.valueOf(problemId));
        assertFalse(Files.exists(problemDir));

        // Act
        String result = service.storeTestCaseOutput(content, problemId, testCaseId);

        // Assert
        assertNotNull(result);
        assertTrue(Files.exists(problemDir));
    }

    @Test
    void storeTestCaseOutput_OverwritesExistingFile() throws IOException {
        // Arrange
        Long problemId = 6L;
        Long testCaseId = 20L;
        String initialContent = "initial output";
        String updatedContent = "updated output";

        // Act
        String path1 = service.storeTestCaseOutput(initialContent, problemId, testCaseId);
        String path2 = service.storeTestCaseOutput(updatedContent, problemId, testCaseId);

        // Assert
        assertEquals(path1, path2);
        assertEquals(updatedContent, Files.readString(Paths.get(path2)));
    }

    @Test
    void deleteTestCasesDirectory_Success_DeletesAllFiles() throws IOException {
        // Arrange
        Long problemId = 7L;
        Path problemDir = tempDir.resolve(String.valueOf(problemId));
        Files.createDirectories(problemDir);

        Files.writeString(problemDir.resolve("testcase_1_input.txt"), "input1");
        Files.writeString(problemDir.resolve("testcase_1_output.txt"), "output1");
        Files.writeString(problemDir.resolve("testcase_2_input.txt"), "input2");

        assertTrue(Files.exists(problemDir));
        assertEquals(3, Files.list(problemDir).count());

        // Act
        service.deleteTestCasesDirectory(problemId);

        // Assert
        assertFalse(Files.exists(problemDir));
    }

    @Test
    void deleteTestCasesDirectory_NonExistentDirectory_DoesNotThrowException() {
        // Arrange
        Long nonExistentProblemId = 9999L;

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> service.deleteTestCasesDirectory(nonExistentProblemId));
    }

    @Test
    void deleteTestCasesDirectory_EmptyDirectory_DeletesDirectory() throws IOException {
        // Arrange
        Long problemId = 8L;
        Path problemDir = tempDir.resolve(String.valueOf(problemId));
        Files.createDirectories(problemDir);

        assertTrue(Files.exists(problemDir));

        // Act
        service.deleteTestCasesDirectory(problemId);

        // Assert
        assertFalse(Files.exists(problemDir));
    }

    @Test
    void deleteTestCasesDirectory_WithNestedDirectories_DeletesAll() throws IOException {
        // Arrange
        Long problemId = 9L;
        Path problemDir = tempDir.resolve(String.valueOf(problemId));
        Path subDir = problemDir.resolve("subdir");
        Files.createDirectories(subDir);

        Files.writeString(problemDir.resolve("file1.txt"), "content1");
        Files.writeString(subDir.resolve("file2.txt"), "content2");

        assertTrue(Files.exists(subDir));

        // Act
        service.deleteTestCasesDirectory(problemId);

        // Assert
        assertFalse(Files.exists(problemDir));
        assertFalse(Files.exists(subDir));
    }

    @Test
    void storeTestCase_FileNameFormat_IsCorrect() {
        // Arrange
        Long problemId = 100L;
        Long testCaseId = 25L;
        MultipartFile file = new MockMultipartFile(
                "file",
                "original.txt",
                "text/plain",
                "content".getBytes()
        );

        // Act
        String result = service.storeTestCase(file, problemId, testCaseId);

        // Assert
        assertNotNull(result);
        assertTrue(result.endsWith("testcase_25_input.txt"));
        assertTrue(result.contains(String.valueOf(problemId)));
    }

    @Test
    void storeTestCaseOutput_FileNameFormat_IsCorrect() {
        // Arrange
        Long problemId = 101L;
        Long testCaseId = 30L;

        // Act
        String result = service.storeTestCaseOutput("output", problemId, testCaseId);

        // Assert
        assertNotNull(result);
        assertTrue(result.endsWith("testcase_30_output.txt"));
        assertTrue(result.contains(String.valueOf(problemId)));
    }
}