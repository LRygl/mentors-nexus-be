package com.mentors.applicationstarter.DTO;

import com.mentors.applicationstarter.Model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseDTO extends BaseEntity {

    private BigDecimal price;
    private String status;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean featured;
    private Boolean published;
    private String level;
    private Set<String> labels;
    private Set<Long> categoryIds;
    private UserResponseDTO owner;
    private List<CourseSectionDTO> sections;
    private int duration;
    private int students;
    private int rating;
    private List<String> goals;
    private List<String> requirements;
}
