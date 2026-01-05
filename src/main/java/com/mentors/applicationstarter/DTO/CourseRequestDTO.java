package com.mentors.applicationstarter.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
@Getter
@Setter
public class CourseRequestDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String status;
    private BigDecimal price;
    private Boolean published;
    private Instant publishedAt;
    private Boolean isFeatured;
    private Long courseOwnerId;
    private Set<String> labels;  // Label names
    private Set<Long> categoryIds;
    private List<String> goals;
    private List<String> requirements;
}
