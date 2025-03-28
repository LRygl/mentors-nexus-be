package com.mentors.applicationstarter.Utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class TimeUtils {

    public static boolean isOlderThan24Hours(String timestampString) {
        try {
            Instant timestamp = Instant.parse(timestampString);
            Instant now = Instant.now();
            Instant threshold = now.minus(Duration.ofHours(24));

            return timestamp.isBefore(threshold);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing time");
            return false;
        }
    }
}
