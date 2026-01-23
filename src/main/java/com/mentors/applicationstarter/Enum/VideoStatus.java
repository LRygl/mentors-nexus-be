package com.mentors.applicationstarter.Enum;

/**
 * Video processing status
 * Tracks the lifecycle of an uploaded video through conversion manager
 */
public enum VideoStatus {
    UPLOADING,   // Initial upload in progress
    PROCESSING,  // Video is being converted to MP4
    READY,       // Video is ready to stream
    FAILED       // Conversion failed (check conversionError field)
}
