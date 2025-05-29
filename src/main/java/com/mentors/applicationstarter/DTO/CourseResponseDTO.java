package com.mentors.applicationstarter.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
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
    private Instant published;
    private String status;
    private String name;
    private Set<String> labels;
    private Set<String> categories;
}
