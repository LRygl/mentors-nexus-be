package com.mentors.applicationstarter.Service;

import java.nio.file.Path;

public interface VideoConversionService {

    /**
     * Queue a video for conversion to MP4
     * This method returns immediately - conversion happens in background
     *
     * @param lessonId - ID of the lesson whose video needs conversion
     */
    void queueVideoConversion(Long lessonId);

    /**
     * Convert a video file to MP4 format
     * This is called asynchronously by queueVideoConversion
     *
     * @param inputPath - Path to original video file
     * @param outputPath - Path where MP4 should be saved
     * @return true if conversion succeeded, false otherwise
     */
    boolean convertToMp4(Path inputPath, Path outputPath);
}
