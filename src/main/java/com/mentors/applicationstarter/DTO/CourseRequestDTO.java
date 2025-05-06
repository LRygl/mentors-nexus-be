package com.mentors.applicationstarter.DTO;

import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class CourseRequestDTO {
    private Long id;
    private String name;
    private String category;
    private String status;
    private String price;
    private Instant published;
    private String courseOwner;
    private Set<String> labels;  // Label names
}
