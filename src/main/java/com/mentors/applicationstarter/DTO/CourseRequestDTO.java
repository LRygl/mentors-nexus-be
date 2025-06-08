package com.mentors.applicationstarter.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Data
@Getter
@Setter
public class CourseRequestDTO {
    private Long id;
    private String name;
    private String category;
    private String status;
    private String price;
    private Instant published;
    private Long courseOwnerId;
    private Set<String> labels;  // Label names
    private Set<String> categories;
}
