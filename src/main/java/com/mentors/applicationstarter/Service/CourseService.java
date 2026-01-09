package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.CourseRequestDTO;
import com.mentors.applicationstarter.DTO.CourseResponseDTO;
import com.mentors.applicationstarter.DTO.CourseStatusDTO;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.CourseSection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public interface CourseService {

    CourseResponseDTO getCourseById(Long courseId);

    List<CourseResponseDTO> getAllCourses();

    CourseResponseDTO createCourse(CourseRequestDTO course, MultipartFile file);
    CourseResponseDTO updateCourse(Long courseId, CourseRequestDTO courseRequestDTO, MultipartFile image);

    CourseResponseDTO deleteCourse(Long courseId);

    CourseResponseDTO updateCourseStatus(Long id, CourseStatusDTO courseStatusDTO);

    Page<CourseResponseDTO> getPagedCourses(String name, Set<String> categoryNames, Pageable pageable);

    CourseResponseDTO createCourseSection(CourseSection section, Long courseId);

    CourseResponseDTO delteCourseSection(Long id);

    CourseResponseDTO addLessonToCourseSection(Long sectionId, Long lessonId);

    CourseResponseDTO reorderCourseSections(List<Long> sectionOrder);

    CourseResponseDTO removeLessonFromCourse(Long sectionId, Long lessonId);

    CourseResponseDTO featureCourse(Long id);

    CourseResponseDTO unfeatureCourse(Long id);

    List<CourseResponseDTO> getAllFeaturedCourses();
}
