package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Constant.FileConstant;
import com.mentors.applicationstarter.Service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

import static com.mentors.applicationstarter.Constant.FileConstant.APPLICATION_ROOT_PATH;
import static com.mentors.applicationstarter.Constant.FileConstant.USER_FOLDER;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    // Base storage directory
    private final Path rootStorageLocation = Paths.get(
            System.getProperty("user.home") + "/mentors"
    ).toAbsolutePath().normalize();

    /**
     * Store file with full relative path
     *
     * @param entityType e.g., "course", "user", "lesson"
     * @param entityUUID the entity's UUID
     * @param fileType e.g., "image", "document", "video"
     * @param file the uploaded file
     * @return relative path from storage root: "/lesson/{uuid}/video/filename.avi"
     */
    @Override
    public String storeFile(String entityType, String fileType, UUID entityUUID, MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        validateFileSize(fileType, file);
        validateMimeType(fileType, file);

        String normalizedEntityType = entityType.toLowerCase();
        String normalizedFileType = fileType.toLowerCase();

        try {
            String originalFilename = StringUtils.cleanPath(
                    Objects.requireNonNull(file.getOriginalFilename())
            );

            if (originalFilename.contains("..")) {
                throw new IllegalArgumentException(
                        "Filename contains invalid path sequence: " + originalFilename
                );
            }

            // Build target directory: /home/user/mentors/lesson/{uuid}/video/
            Path targetDir = rootStorageLocation.resolve(
                    Paths.get(normalizedEntityType, String.valueOf(entityUUID), normalizedFileType)
            );

            Files.createDirectories(targetDir);
            LOGGER.info("Target directory: {}", targetDir);

            String sanitizedFileName = sanitizeFilename(file.getOriginalFilename());
            Path targetFile = targetDir.resolve(sanitizedFileName);

            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("File stored at: {}", targetFile);

            // IMPORTANT: Build the relative path manually using string formatting
            // DO NOT use targetFile.toString() as it returns absolute path
            String relativePath = String.format("/%s/%s/%s/%s",
                    normalizedEntityType,   // "lesson"
                    entityUUID,             // "4ba1e3b7-9f7a-46ac-b4d9-bee9e3ca4af6"
                    normalizedFileType,     // "video"
                    sanitizedFileName);     // "c-erveny-trpasli-k-01-konec.avi"

            // Result: /lesson/4ba1e3b7-9f7a-46ac-b4d9-bee9e3ca4af6/video/c-erveny-trpasli-k-01-konec.avi

            LOGGER.info("Relative path to store in DB: {}", relativePath);
            return relativePath;

        } catch (IOException e) {
            LOGGER.error("Failed to store file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void createEntityDirectory(String entityName, String entityDescription) {
        Path path = rootStorageLocation.resolve(
                Paths.get(entityName, entityDescription)
        ).toAbsolutePath().normalize();

        try {
            Files.createDirectories(path);
            LOGGER.info("Directory created: {}", path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory for entity: " + path, e);
        }
    }

    /**
     * Load file as Resource for serving
     */
    @Override
    public Resource loadFileAsResource(String relativePath) {
        try {
            String cleanPath = relativePath.startsWith("/")
                    ? relativePath.substring(1)
                    : relativePath;

            LOGGER.info("Loading resource - Clean path: {}", cleanPath);
            LOGGER.info("Root storage: {}", rootStorageLocation);

            Path filePath = rootStorageLocation
                    .resolve(cleanPath)
                    .normalize();

            LOGGER.info("Full file path: {}", filePath);

            // Security check
            if (!filePath.startsWith(rootStorageLocation)) {
                throw new SecurityException("Cannot access file outside storage directory");
            }

            if (!Files.exists(filePath)) {
                LOGGER.error("File does not exist: {}", filePath);
                throw new FileNotFoundException("File not found: " + relativePath);
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                LOGGER.info("Resource loaded successfully");
                return resource;
            } else {
                throw new FileNotFoundException("File not found: " + relativePath);
            }

        } catch (MalformedURLException e) {
            LOGGER.error("Malformed URL for path: {}", relativePath, e);
            throw new RuntimeException("Error loading file", e);
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found: {}", relativePath, e);
            throw new RuntimeException("File not found: " + relativePath, e);
        }
    }

    //
    // PRIVATE
    //

    private String sanitizeFilename(String filename) {
        String nameWithoutExt = filename.substring(0, filename.lastIndexOf('.'));
        String extension = filename.substring(filename.lastIndexOf('.'));

        String sanitized = nameWithoutExt
                .replaceAll("[^a-zA-Z0-9.-]", "-")
                .replaceAll("-+", "-")
                .toLowerCase();

        return sanitized + extension;
    }

    private void validateFileSize(String fileType, MultipartFile file) {
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

    private void validateMimeType(String fileType, MultipartFile file) {
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
}