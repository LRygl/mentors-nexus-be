package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Constant.FileConstant;
import com.mentors.applicationstarter.Service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
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

@Profile("dev")
@Service
@Slf4j
public class LocalFileStorageServiceImpl extends AbstractFileStorageService {


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
            log.info("Target directory: {}", targetDir);

            String sanitizedFileName = sanitizeFilename(file.getOriginalFilename());
            Path targetFile = targetDir.resolve(sanitizedFileName);

            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("File stored at: {}", targetFile);

            // IMPORTANT: Build the relative path manually using string formatting
            // DO NOT use targetFile.toString() as it returns absolute path
            String relativePath = String.format("/%s/%s/%s/%s",
                    normalizedEntityType,   // "lesson"
                    entityUUID,             // "4ba1e3b7-9f7a-46ac-b4d9-bee9e3ca4af6"
                    normalizedFileType,     // "video"
                    sanitizedFileName);     // "c-erveny-trpasli-k-01-konec.avi"

            // Result: /lesson/4ba1e3b7-9f7a-46ac-b4d9-bee9e3ca4af6/video/c-erveny-trpasli-k-01-konec.avi

            log.info("Relative path to store in DB: {}", relativePath);
            return relativePath;

        } catch (IOException e) {
            log.error("Failed to store file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }

    /**
     * Load file as Resource for serving (used by video streaming)
     * This is a BONUS method specific to local storage - not in interface
     */
    public Resource loadFileAsResource(String relativePath) {
        try {
            String cleanPath = relativePath.startsWith("/")
                    ? relativePath.substring(1)
                    : relativePath;

            Path filePath = rootStorageLocation
                    .resolve(cleanPath)
                    .normalize();

            // Security check
            if (!filePath.startsWith(rootStorageLocation)) {
                throw new SecurityException("Cannot access file outside storage directory");
            }

            if (!Files.exists(filePath)) {
                throw new FileNotFoundException("File not found: " + relativePath);
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found: " + relativePath);
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException("Error loading file", e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + relativePath, e);
        }
    }

    @Override
    public String generatePresignedUrl(String filePath, int expirationMinutes) {
        // For local development, just return the relative path
        // Your controller will serve it directly
        log.debug("Local storage - returning path directly: {}", filePath);
        return filePath;
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            String cleanPath = filePath.startsWith("/")
                    ? filePath.substring(1)
                    : filePath;

            Path fileToDelete = rootStorageLocation.resolve(cleanPath).normalize();

            // Security check
            if (!fileToDelete.startsWith(rootStorageLocation)) {
                throw new SecurityException("Cannot delete file outside storage directory");
            }

            Files.deleteIfExists(fileToDelete);
            log.info("Deleted local file: {}", fileToDelete);

        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    @Override
    public boolean fileExists(String filePath) {
        try {
            String cleanPath = filePath.startsWith("/")
                    ? filePath.substring(1)
                    : filePath;

            Path file = rootStorageLocation.resolve(cleanPath).normalize();
            return Files.exists(file);

        } catch (Exception e) {
            log.error("Error checking if file exists: {}", filePath, e);
            return false;
        }
    }


}