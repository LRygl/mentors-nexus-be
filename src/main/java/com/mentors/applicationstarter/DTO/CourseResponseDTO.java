package com.mentors.applicationstarter.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class CourseResponseDTO {
    private Long id;
    private UUID uuid;
    private String name;
    private Set<String> labels;
}
