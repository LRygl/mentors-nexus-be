package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.Service.FileStorageService;
import com.mentors.applicationstarter.Service.Impl.LocalFileStorageServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @GetMapping("/**")
    public ResponseEntity<?> serveFile(HttpServletRequest request) {
        try {
            // Extract file path from URL
            // Example: /api/v1/files/lesson/uuid/video/file.mp4 → lesson/uuid/video/file.mp4
            String requestPath = request.getRequestURI().substring("/api/v1/files/".length());
            String decodedPath = URLDecoder.decode(requestPath, StandardCharsets.UTF_8);

            if ("dev".equals(activeProfile)) {
                // DEV MODE: Serve file directly from local filesystem
                return serveLocalFile(decodedPath);
            } else {
                // PROD MODE: Redirect to S3 presigned URL
                return redirectToS3(decodedPath);
            }

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * DEV MODE: Serve file from local filesystem
     */
    private ResponseEntity<Resource> serveLocalFile(String filePath) {
        try {
            // Cast to LocalFileStorageServiceImpl to access loadFileAsResource
            // This is safe because we only call this in dev profile
            if (fileStorageService instanceof LocalFileStorageServiceImpl localStorage) {

                // Add leading slash if not present (for consistency)
                String pathToLoad = filePath.startsWith("/") ? filePath : "/" + filePath;

                Resource resource = localStorage.loadFileAsResource(pathToLoad);
                String contentType = determineContentType(resource.getFilename());

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
                        .header(
                                HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + resource.getFilename() + "\""
                        )
                        .body(resource);
            } else {
                throw new IllegalStateException("Local file serving not available in this profile");
            }

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PROD MODE: Redirect to S3 presigned URL
     */
    private ResponseEntity<Void> redirectToS3(String s3Key) {
        try {
            // Remove leading slash from S3 key if present
            String cleanKey = s3Key.startsWith("/") ? s3Key.substring(1) : s3Key;

            // Generate presigned URL valid for 1 hour
            String presignedUrl = fileStorageService.generatePresignedUrl(cleanKey, 60);

            // Return 302 redirect to S3 presigned URL
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(presignedUrl))
                    .cacheControl(CacheControl.noCache()) // Don't cache redirects
                    .build();

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get file content type
     */
    private String determineContentType(String filename) {
        if (filename == null) return "application/octet-stream";

        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".pdf")) return "application/pdf";

        return "application/octet-stream";
    }
}
