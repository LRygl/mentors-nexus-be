package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.Course.EnrolledCourseDTO;
import com.mentors.applicationstarter.Model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface CourseEnrollmentService {
    Set<Long> getEnrolledCourseIds(Long id);

    void unenroll(Long id, Long courseId);

    EnrolledCourseDTO enroll(User user, Long courseId);

    boolean isEnrolled(Long id, Long courseId);

    List<EnrolledCourseDTO> getEnrollments(Long id);
}
