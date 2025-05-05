package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.CourseRequestDTO;
import com.mentors.applicationstarter.DTO.CourseResponseDTO;
import com.mentors.applicationstarter.Model.Course;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseService {
    CourseResponseDTO createCourse(CourseRequestDTO course);

    CourseResponseDTO getCourseById(Long courseId);

    List<CourseResponseDTO> getAllCourses();
}
