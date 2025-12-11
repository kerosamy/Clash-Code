package com.clashcode.backend.service;

import com.clashcode.backend.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageFileStorageService {

    private final Path fileStorageLocation;

    public ImageFileStorageService(@Value("${CLASH_CODE_FS_PATH}") String baseUploadDir) {
        // Create profile-images subfolder within CLASH_CODE_FS_PATH
        this.fileStorageLocation = Paths.get(baseUploadDir, "profile-images")
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create upload directory: " + this.fileStorageLocation, ex);
        }
    }

    public String storeFile(MultipartFile file, String username) {
        // Validate file
        if (file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FileStorageException("Only image files are allowed");
        }

        // Get file extension
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        if (originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Create unique filename: username_timestamp_uuid.ext
        String fileName = username + "_" + System.currentTimeMillis() + "_" +
                UUID.randomUUID().toString().substring(0, 8) + fileExtension;

        try {
            // Check for invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + fileName);
            }

            // Copy file to target location
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName, ex);
        }
    }

    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + fileName, ex);
        }
    }

    public Path loadFile(String fileName) {
        return this.fileStorageLocation.resolve(fileName).normalize();
    }
}