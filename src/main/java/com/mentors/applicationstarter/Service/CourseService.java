package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.CourseRequestDTO;
import com.mentors.applicationstarter.DTO.CourseResponseDTO;
import com.mentors.applicationstarter.DTO.CourseStatusDTO;
import com.mentors.applicationstarter.Model.Course;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseService {
    CourseResponseDTO createCourse(CourseRequestDTO course);

    CourseResponseDTO getCourseById(Long courseId);

    List<CourseResponseDTO> getAllCourses();

    CourseResponseDTO updateCourse(CourseRequestDTO courseRequestDTO);

    CourseResponseDTO deleteCourse(Long courseId);

    CourseResponseDTO updateCourseStatus(Long id, CourseStatusDTO courseStatusDTO);
}
