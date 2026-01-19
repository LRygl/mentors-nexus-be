package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.Lesson;
import com.mentors.applicationstarter.Repository.LessonRepository;
import com.mentors.applicationstarter.Service.FileStorageService;
import com.mentors.applicationstarter.Service.VideoStreamingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class VideoStreamingServiceImpl implements VideoStreamingService {

    //TODO FIX ERROR CODE MAPPING
    private final LessonRepository lessonRepository;
    private final FileStorageService fileStorageService;

    private final Path rootStorageLocation = Paths.get(
            System.getProperty("user.home") + "/mentors"
    ).toAbsolutePath().normalize();

    @Override
    public ResponseEntity<Resource> prepareContent(String lessonUuid, String range) {
        try {
            // Find lesson
            Lesson lesson = lessonRepository.findByUuid(UUID.fromString(lessonUuid))
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.CATEGORY_DOES_NOT_EXIST));

            if (lesson.getVideoUrl() == null) {
                throw new ResourceNotFoundException(ErrorCodes.CATEGORY_DOES_NOT_EXIST);
            }

            // Get file path
            String cleanPath = lesson.getVideoUrl().startsWith("/")
                    ? lesson.getVideoUrl().substring(1)
                    : lesson.getVideoUrl();

            Path videoPath = rootStorageLocation.resolve(cleanPath).normalize();

            if (!Files.exists(videoPath)) {
                throw new ResourceNotFoundException(ErrorCodes.CATEGORY_DOES_NOT_EXIST);
            }

            File videoFile = videoPath.toFile();
            long fileSize = videoFile.length();
            String contentType = determineContentType(lesson.getVideoUrl());

            // If no range header, return full video
            if (range == null || range.isEmpty()) {
                InputStreamResource resource = new InputStreamResource(new FileInputStream(videoFile));

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .contentLength(fileSize)
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .body(resource);
            }

            // Parse range
            String[] ranges = range.replace("bytes=", "").split("-");
            long rangeStart = Long.parseLong(ranges[0]);
            long rangeEnd;

            if (ranges.length > 1 && !ranges[1].isEmpty()) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = fileSize - 1;
            }

            if (rangeStart > fileSize - 1 || rangeEnd > fileSize - 1) {
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                        .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize)
                        .body(null);
            }

            long contentLength = rangeEnd - rangeStart + 1;

            log.info("Serving range: bytes {}-{}/{} ({} bytes)", rangeStart, rangeEnd, fileSize, contentLength);

            // Create input stream that starts at the requested byte
            RandomAccessFile randomAccessFile = new RandomAccessFile(videoFile, "r");
            randomAccessFile.seek(rangeStart);

            InputStream inputStream = new InputStream() {
                private long bytesRead = 0;

                @Override
                public int read() throws IOException {
                    if (bytesRead >= contentLength) {
                        return -1;
                    }
                    bytesRead++;
                    return randomAccessFile.read();
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    if (bytesRead >= contentLength) {
                        return -1;
                    }

                    long remaining = contentLength - bytesRead;
                    int toRead = (int) Math.min(len, remaining);
                    int actuallyRead = randomAccessFile.read(b, off, toRead);

                    if (actuallyRead > 0) {
                        bytesRead += actuallyRead;
                    }

                    return actuallyRead;
                }

                @Override
                public void close() throws IOException {
                    randomAccessFile.close();
                }
            };

            InputStreamResource resource = new InputStreamResource(inputStream);

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(contentLength)
                    .header(HttpHeaders.CONTENT_RANGE,
                            String.format("bytes %d-%d/%d", rangeStart, rangeEnd, fileSize))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .body(resource);

        } catch (IOException e) {
            log.error("Error streaming video: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String determineContentType(String videoUrl) {
        String fileName = videoUrl.toLowerCase();
        if (fileName.endsWith(".mp4")) return "video/mp4";
        if (fileName.endsWith(".webm")) return "video/webm";
        if (fileName.endsWith(".avi")) return "video/x-msvideo";
        if (fileName.endsWith(".mov")) return "video/quicktime";
        if (fileName.endsWith(".mkv")) return "video/x-matroska";
        return "application/octet-stream";
    }
}
