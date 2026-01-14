package com.mentors.applicationstarter.DTO.Course;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrolledCourseDTO {
    private Long courseId;
    private String courseName;
    private String courseImageUrl;
    private Instant enrolledAt;
    private Double progress; // optional: track completion percentage
}