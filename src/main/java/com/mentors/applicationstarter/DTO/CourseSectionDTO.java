package com.mentors.applicationstarter.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSectionDTO {
    private Long id;
    private UUID uuid;
    private String title;
    private String description;
    private Integer orderIndex;
    private List<LessonDTO> lessons;
}
