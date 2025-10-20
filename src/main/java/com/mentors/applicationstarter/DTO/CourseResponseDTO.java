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
    private Instant created;
    private Instant updated;
    private Instant published;
    private BigDecimal price;
    private String status;
    private String name;
    private Set<String> labels;
    private Set<String> categories;
    private UserResponseDTO owner;
    private List<CourseSectionDTO> sections;
    private int students;
}
