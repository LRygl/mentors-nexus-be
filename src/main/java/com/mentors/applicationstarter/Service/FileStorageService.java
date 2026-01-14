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
     * Create base directory for a new entity
     */
    void createEntityDirectory(String entityName, String entityDescription);

    Resource loadFileAsResource(String decodedPath);
}
