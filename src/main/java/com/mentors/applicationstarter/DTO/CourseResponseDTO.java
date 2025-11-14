package com.mentors.applicationstarter.DTO;

import com.mentors.applicationstarter.Model.CourseSection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseDTO {
    private Long id;
    private UUID uuid;

    private Instant published;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private UUID updatedBy;

    private BigDecimal price;
    private String status;
    private String name;
    private String imageUrl;
    private Boolean isFeatured;
    private Set<String> labels;
    private Set<Long> categoryIds;
    private UserResponseDTO owner;
    private List<CourseSectionDTO> sections;
    private int students;
}
