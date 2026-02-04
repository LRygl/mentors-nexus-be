package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Constant.FileConstant;
import com.mentors.applicationstarter.Service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract base class containing shared validation logic
 */

@Slf4j
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
        String filename = file.getOriginalFilename();

        log.info("Validating file - Name: {}, ContentType: {}, FileType: {}",
                filename, contentType, fileType);

        if (contentType == null || contentType.isEmpty()) {
            log.warn("No content type provided, validating by file extension only");
            validateByFileExtension(filename, fileType);
            return;
        }

        // Handle cases where Content-Type is generic (Postman/browser quirks)
        String baseType = contentType.split(";")[0].trim().toLowerCase();

        if (baseType.equals("multipart/form-data") ||
                baseType.equals("application/octet-stream") ||
                baseType.equals("binary/octet-stream")) {
            log.warn("Received generic content type ({}), validating by file extension instead", baseType);
            validateByFileExtension(filename, fileType);
            return;
        }

        // Normal MIME type validation
        switch (fileType.toLowerCase()) {
            case "image" -> {
                if (!baseType.startsWith("image/")) {
                    throw new IllegalArgumentException(
                            "Invalid image file type: " + baseType
                    );
                }
            }
            case "video" -> {
                if (!baseType.startsWith("video/")) {
                    throw new IllegalArgumentException(
                            "Invalid video file type: " + baseType
                    );
                }
            }
            case "document" -> {
                List<String> allowedTypes = Arrays.asList(
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                );
                if (!allowedTypes.contains(baseType)) {
                    throw new IllegalArgumentException(
                            "Invalid document file type: " + baseType
                    );
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

    /**
     * Validate file by extension when Content-Type is unreliable
     */
    private void validateByFileExtension(String filename, String fileType) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("Invalid filename: " + filename);
        }

        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        switch (fileType.toLowerCase()) {
            case "image" -> {
                List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg");
                if (!allowedExtensions.contains(extension)) {
                    throw new IllegalArgumentException(
                            "Invalid image file extension: " + extension + ". Allowed: " + allowedExtensions
                    );
                }
                // Optional: Validate magic bytes for extra security
                validateImageMagicBytes(extension);
            }
            case "video" -> {
                List<String> allowedExtensions = Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "mkv", "webm");
                if (!allowedExtensions.contains(extension)) {
                    throw new IllegalArgumentException(
                            "Invalid video file extension: " + extension + ". Allowed: " + allowedExtensions
                    );
                }
            }
            case "document" -> {
                List<String> allowedExtensions = Arrays.asList("pdf", "doc", "docx", "txt");
                if (!allowedExtensions.contains(extension)) {
                    throw new IllegalArgumentException(
                            "Invalid document file extension: " + extension + ". Allowed: " + allowedExtensions
                    );
                }
            }
            default -> throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }

        log.info("File validated by extension: {} ({})", filename, extension);
    }


    /**
     * Optional: Validate image file content by checking magic bytes
     * This provides extra security against file type spoofing
     */
    private void validateImageMagicBytes(String extension) {
        // For now, just log - you can implement actual magic byte checking if needed
        log.debug("Image extension validated: {}", extension);
    }
}
