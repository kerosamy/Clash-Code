package com.clashcode.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.clashcode.backend.exception.FileStorageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageFileStorageService {

    private final Cloudinary cloudinary;

    public ImageFileStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String storeFile(MultipartFile file, String username) {
        if (file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FileStorageException("Only image files are allowed");
        }

        String publicId = "profile-images/" + username + "_" +
                System.currentTimeMillis() + "_" +
                UUID.randomUUID().toString().substring(0, 8);

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "image",
                            "overwrite", true
                    ));

            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new FileStorageException("Could not upload file to Cloudinary", e);
        }
    }

    public void deleteFile(String publicUrlOrPublicId) {
        if (publicUrlOrPublicId == null || publicUrlOrPublicId.isEmpty()) return;

        try {
            String publicId = publicUrlOrPublicId;
            if (publicUrlOrPublicId.startsWith("http")) {
                // Remove Cloudinary URL prefix and version
                publicId = publicUrlOrPublicId
                        .replaceAll("^https://res\\.cloudinary\\.com/[^/]+/image/upload/v[0-9]+/", "")
                        .replaceAll("\\.[a-zA-Z0-9]+$", "");
            }

            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));

        } catch (IOException e) {
            throw new FileStorageException("Could not delete file from Cloudinary", e);
        }
    }

}
