package com.mentors.applicationstarter.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class CourseResponseDTO {
    private Long id;
    private UUID uuid;
    private Instant created;
    private Instant published;
    private String status;
    private String name;
    private Set<String> labels;
}
