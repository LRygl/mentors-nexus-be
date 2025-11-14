package com.mentors.applicationstarter.Service.Impl;

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
     * @return relative path from storage root: "course/{uuid}/image/filename.png"
     */
    @Override
    public String storeFile(String entityType, String fileType, UUID entityUUID, MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String nomalizedEntityType = entityType.toLowerCase();
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

            // Build target directory path: e.g. /uploads/{uuid}/images/
            Path targetDir = Paths.get(nomalizedEntityType, String.valueOf(entityUUID), normalizedFileType);
            Files.createDirectories(targetDir);

            // Get sanitized file name
            String sanitizedFileName = sanitizeFilename(file.getOriginalFilename());

            // Resolve full file path
            Path targetFile = targetDir.resolve(StringUtils.cleanPath(sanitizedFileName));

            // Save file to disk
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            return String.format("/%s/%s/%s", entityUUID, normalizedFileType, sanitizedFileName);

        } catch (IOException e) {
            throw new RuntimeException("Exception");
        }

    }

    @Override
    public void createEntityDirectory(String entityName, String entityDescription) {

        Path path = Paths.get(entityName + "/" + entityDescription).toAbsolutePath().normalize();

        try {
            Files.createDirectories(path);
            System.out.println("Directory created: " + path);
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
            // Remove leading slash if present
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


    private String sanitizeFilename(String filename) {
        String nameWithoutExt = filename.substring(0, filename.lastIndexOf('.'));
        String extension = filename.substring(filename.lastIndexOf('.'));

        String sanitized = nameWithoutExt
                .replaceAll("[^a-zA-Z0-9.-]", "-")
                .replaceAll("-+", "-")
                .toLowerCase();

        return sanitized + extension;
    }
}