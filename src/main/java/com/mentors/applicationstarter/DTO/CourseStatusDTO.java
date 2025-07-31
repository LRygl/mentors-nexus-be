package com.mentors.applicationstarter.DTO;

import com.mentors.applicationstarter.Enum.CourseStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class CourseStatusDTO {
    private CourseStatus status;
    private Instant published;
}
