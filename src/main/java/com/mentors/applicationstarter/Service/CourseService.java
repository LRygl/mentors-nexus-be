package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.CourseRequestDTO;
import com.mentors.applicationstarter.DTO.CourseResponseDTO;
import com.mentors.applicationstarter.DTO.CourseStatusDTO;
import com.mentors.applicationstarter.Model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public interface CourseService {
    CourseResponseDTO createCourse(CourseRequestDTO course);

    CourseResponseDTO getCourseById(Long courseId);

    List<CourseResponseDTO> getAllCourses();

    CourseResponseDTO updateCourse(CourseRequestDTO courseRequestDTO);

    CourseResponseDTO deleteCourse(Long courseId);

    CourseResponseDTO updateCourseStatus(Long id, CourseStatusDTO courseStatusDTO);

    Page<CourseResponseDTO> getPagedCourses(String name, Set<String> categoryNames, Pageable pageable);

    void enrollUserToCourse(Long courseId, UUID userUUID);

    CourseResponseDTO addLessonToCourse(Long courseId, Long lessonId);
}
