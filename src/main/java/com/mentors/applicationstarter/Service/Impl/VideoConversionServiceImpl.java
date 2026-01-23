package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Enum.VideoStatus;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.Lesson;
import com.mentors.applicationstarter.Repository.LessonRepository;
import com.mentors.applicationstarter.Service.VideoConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoConversionServiceImpl implements VideoConversionService {

    private final LessonRepository lessonRepository;

    // Base storage directory (same as FileStorageService)
    private final Path rootStorageLocation = Paths.get(
            System.getProperty("user.home") + "/mentors"
    ).toAbsolutePath().normalize();

    /**
     * Queue video for conversion
     * This method returns immediately - the @Async annotation makes it run in background
     *
     * Think of this like submitting a job to a queue in RabbitMQ - the caller doesn't wait
     */
    @Async("videoConversionExecutor")
    @Transactional
    @Override
    public void queueVideoConversion(Long lessonId) {
        log.info("=== Starting Video Conversion Job ===");
        log.info("Lesson ID: {}", lessonId);

        try {
            // Load lesson
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.LESSON_NOT_FOUND));

            // Check if conversion is needed
            if (lesson.getOriginalVideoUrl() == null) {
                log.warn("No original video URL for lesson {}", lessonId);
                return;
            }

            // Check if already MP4
            if (lesson.getOriginalVideoUrl().toLowerCase().endsWith(".mp4")) {
                log.info("Video is already MP4, no conversion needed");
                lesson.setVideoStatus(VideoStatus.READY);
                lesson.setVideoUrl(lesson.getOriginalVideoUrl());
                lessonRepository.save(lesson);
                return;
            }

            // Update status to PROCESSING
            lesson.setVideoStatus(VideoStatus.PROCESSING);
            lessonRepository.save(lesson);

            // Build file paths
            Path inputPath = rootStorageLocation
                    .resolve(lesson.getOriginalVideoUrl().substring(1)) // Remove leading /
                    .normalize();

            // Output path: same location but with .mp4 extension
            String outputPathStr = lesson.getOriginalVideoUrl()
                    .replaceFirst("\\.[^.]+$", ".mp4"); // Replace extension with .mp4

            Path outputPath = rootStorageLocation
                    .resolve(outputPathStr.substring(1))
                    .normalize();

            log.info("Input: {}", inputPath);
            log.info("Output: {}", outputPath);

            // Verify input exists
            if (!Files.exists(inputPath)) {
                throw new IOException("Input file not found: " + inputPath);
            }

            // Convert video
            boolean success = convertToMp4(inputPath, outputPath);

            if (success) {
                // Update lesson with new MP4 URL
                lesson.setVideoUrl(outputPathStr);
                lesson.setVideoStatus(VideoStatus.READY);
                lesson.setConversionError(null);
                log.info("✅ Video conversion successful for lesson {}", lessonId);
            } else {
                lesson.setVideoStatus(VideoStatus.FAILED);
                lesson.setConversionError("FFmpeg conversion failed - check logs");
                log.error("❌ Video conversion failed for lesson {}", lessonId);
            }

            lessonRepository.save(lesson);
            log.info("=== Video Conversion Job Complete ===");

        } catch (Exception e) {
            log.error("Error during video conversion for lesson {}: {}", lessonId, e.getMessage(), e);

            // Update lesson with error
            try {
                Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
                if (lesson != null) {
                    lesson.setVideoStatus(VideoStatus.FAILED);
                    lesson.setConversionError(e.getMessage());
                    lessonRepository.save(lesson);
                }
            } catch (Exception ex) {
                log.error("Failed to update lesson status: {}", ex.getMessage());
            }
        }
    }

    /**
     * Convert video to MP4 using FFmpeg
     *
     * Concept: This runs a shell command (like running 'mvn install' from Java)
     * FFmpeg is a command-line tool, so we execute it as a subprocess
     */
    @Override
    public boolean convertToMp4(Path inputPath, Path outputPath) {
        try {
            // Build FFmpeg command
            List<String> command = new ArrayList<>();
            command.add("ffmpeg");
            command.add("-i");
            command.add(inputPath.toString());

            // Video codec: H.264 (widely supported)
            command.add("-c:v");
            command.add("libx264");

            // Audio codec: AAC (widely supported)
            command.add("-c:a");
            command.add("aac");

            // Optimize for web streaming (move metadata to beginning)
            command.add("-movflags");
            command.add("+faststart");

            // Quality preset (faster = lower quality but quicker conversion)
            command.add("-preset");
            command.add("medium");

            // Overwrite output file if exists
            command.add("-y");

            command.add(outputPath.toString());

            log.info("FFmpeg command: {}", String.join(" ", command));

            // Execute FFmpeg
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true); // Merge stdout and stderr

            Process process = processBuilder.start();

            // Read output
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("FFmpeg: {}", line);
                }
            }

            // Wait for completion
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("FFmpeg conversion completed successfully");

                // Verify output file was created
                if (Files.exists(outputPath) && Files.size(outputPath) > 0) {
                    log.info("Output file verified: {} ({} bytes)",
                            outputPath, Files.size(outputPath));
                    return true;
                } else {
                    log.error("Output file not created or is empty");
                    return false;
                }
            } else {
                log.error("FFmpeg exited with code: {}", exitCode);
                return false;
            }

        } catch (IOException | InterruptedException e) {
            log.error("Error executing FFmpeg: {}", e.getMessage(), e);
            return false;
        }
    }
}