package com.mentors.applicationstarter.Service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileStorageService {

    /**
     * Save a file under a given course-specific folder and type (e.g. "images", "videos", "documents")
     * @param filePath path to the file
     * @param fileType subfolder (e.g. "images", "videos", "docs")
     * @param file multipart file to store
     * @return public URL to the stored file
     */
    String storeFile(String filePath, String fileType, UUID entityUUID, MultipartFile file);

    /**
     * Generate URL for accessing file
     *
     * For local storage: returns the relative path
     * For S3: returns presigned URL that expires
     *
     * @param filePath the file path/S3 key
     * @param expirationMinutes how long the URL should be valid (ignored for local)
     * @return URL or path to access the file
     */
    String generatePresignedUrl(String filePath, int expirationMinutes);

    /**
     * Delete file from storage
     *
     * @param filePath the file path/S3 key
     */
    void deleteFile(String filePath);

    /**
     * Check if file exists in storage
     *
     * @param filePath the file path/S3 key
     * @return true if file exists
     */
    boolean fileExists(String filePath);


}
