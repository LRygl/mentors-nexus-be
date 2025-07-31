package com.mentors.applicationstarter.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonDTO {
    private Long id; // Nullable for new lessons
    private UUID uuid;
    private String title;
    private String description;
    private String videoUrl;
    private Duration duration;
    private Integer orderIndex;
    private CourseSummaryDTO course;
}
