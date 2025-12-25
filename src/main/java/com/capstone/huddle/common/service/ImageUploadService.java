package com.capstone.huddle.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class ImageUploadService {

    @Value("${upload.path:uploads/profiles}")
    private String uploadPath;

    @Value("${server.base-url:http://localhost:6061}")  // Changed from 8080 to 6061
    private String serverBaseUrl;

    public String uploadProfilePicture(MultipartFile file, String username) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Validate file
        validateImageFile(file);

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = username + "_" + UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = uploadDir.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath);

        log.info("Profile picture uploaded for user {}: {}", username, newFilename);

        // Return URL with correct port
        return serverBaseUrl + "/huddle/uploads/profiles/" + newFilename;
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // Max size: 5MB
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be less than 5MB");
        }
    }

    public void deleteProfilePicture(String fileUrl) {
        if (fileUrl == null || !fileUrl.contains("/uploads/profiles/")) {
            return;
        }

        try {
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadPath).resolve(filename);
            Files.deleteIfExists(filePath);
            log.info("Deleted profile picture: {}", filename);
        } catch (Exception e) {
            log.error("Failed to delete profile picture: {}", fileUrl, e);
        }
    }
}