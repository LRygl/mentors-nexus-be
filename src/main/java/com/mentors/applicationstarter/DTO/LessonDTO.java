package com.mentors.applicationstarter.DTO;

import com.mentors.applicationstarter.Enum.LessonCategory;
import com.mentors.applicationstarter.Enum.LessonType;
import com.mentors.applicationstarter.Enum.VideoStatus;
import com.mentors.applicationstarter.Model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LessonDTO extends BaseEntity {
    private Long id; // Nullable for new lessons
    private UUID uuid;
    private String title;
    private String description;
    private String imageUrl;
    private String videoUrl;
    private Integer duration;
    private Integer orderIndex;
    private Instant createdAt;
    private Instant updatedAt;
    private VideoStatus videoStatus;
    private LessonType type;
    private LessonCategory category;
}
