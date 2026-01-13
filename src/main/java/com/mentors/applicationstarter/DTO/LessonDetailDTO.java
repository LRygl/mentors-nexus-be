package com.mentors.applicationstarter.DTO;

import com.mentors.applicationstarter.Enum.LessonCategory;
import com.mentors.applicationstarter.Enum.LessonType;
import com.mentors.applicationstarter.Model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDetailDTO extends BaseEntity {
    private String title;
    private String description;
    private String imageUrl;
    private String videoUrl;
    private Integer duration;
    private Integer orderIndex;
    private LessonType type;
    private LessonCategory category;

    private CourseSectionSummaryDTO section;
    private CourseSummaryDTO course;
}
