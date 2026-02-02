package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Constant.FileConstant;
import com.mentors.applicationstarter.Service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;

/**
 * Abstract base class containing shared validation logic
 */

public abstract class AbstractFileStorageService implements FileStorageService {

    protected void validateFileSize(String fileType, MultipartFile file) {
        long size = file.getSize();
        switch (fileType.toLowerCase()) {
            case "image" -> {
                if (size > FileConstant.IMAGE_MAX_SIZE) {
                    throw new IllegalArgumentException("Image file exceeds 5 MB limit");
                }
            }
            case "video" -> {
                if (size > FileConstant.VIDEO_MAX_SIZE) {
                    throw new IllegalArgumentException("Video file exceeds 200 MB limit");
                }
            }
            case "document" -> {
                if (size > FileConstant.DOCUMENT_MAX_SIZE) {
                    throw new IllegalArgumentException("Document file exceeds 10 MB limit");
                }
            }
            default -> throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }

    protected void validateMimeType(String fileType, MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Missing content type");
        }

        switch (fileType.toLowerCase()) {
            case "image" -> {
                if (!contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("Invalid image file type");
                }
            }
            case "video" -> {
                if (!contentType.startsWith("video/")) {
                    throw new IllegalArgumentException("Invalid video file type");
                }
            }
        }
    }

    protected String sanitizeFilename(String filename) {
        String nameWithoutExt = filename.substring(0, filename.lastIndexOf('.'));
        String extension = filename.substring(filename.lastIndexOf('.'));

        String sanitized = nameWithoutExt
                .replaceAll("[^a-zA-Z0-9.-]", "-")
                .replaceAll("-+", "-")
                .toLowerCase();

        return sanitized + extension;
    }
}
