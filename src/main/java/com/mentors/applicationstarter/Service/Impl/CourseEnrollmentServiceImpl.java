package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.Course.EnrolledCourseDTO;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.BusinessRuleViolationException;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.CourseEnrollment;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Repository.CourseEnrollmentRepository;
import com.mentors.applicationstarter.Repository.CourseRepository;
import com.mentors.applicationstarter.Service.CourseEnrollmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseEnrollmentServiceImpl implements CourseEnrollmentService {

    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final CourseRepository courseRepository;

    /**
     * Get all course IDs the user is enrolled in.
     * This is optimized for caching on the frontend.
     */
    public Set<Long> getEnrolledCourseIds(Long userId) {
        return courseEnrollmentRepository.findCourseIdsByUserId(userId);
    }

    /**
     * Get detailed enrollment info for a user.
     */
    public List<EnrolledCourseDTO> getEnrollments(Long userId) {
        return courseEnrollmentRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .toList();
    }


    /**
     * Check if user is enrolled in a specific course.
     */
    public boolean isEnrolled(Long userId, Long courseId) {
        return courseEnrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    /**
     * Enroll a user in a course.
     */
    @Transactional
    public EnrolledCourseDTO enroll(User user, Long courseId, CourseEnrollment.EnrollmentType type) {
        // Check if already enrolled
        if (courseEnrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new BusinessRuleViolationException(ErrorCodes.USER_ALREADY_ENROLLED_TO_COURSE);
        }

        // Get the course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResolutionException("Course not found: " + courseId));

        // Create enrollment
        CourseEnrollment enrollment = CourseEnrollment.builder()
                .user(user)
                .course(course)
                .enrolledAt(Instant.now())
                .type(type)
                .progressPercent(0.0)
                .build();

        enrollment = courseEnrollmentRepository.save(enrollment);

        log.info("User {} enrolled in course {} via {}", user.getId(), courseId, type);

        return toDTO(enrollment);
    }

    /**
     * Enroll user (default: purchased).
     */
    @Transactional
    public EnrolledCourseDTO enroll(User user, Long courseId) {
        return enroll(user, courseId, CourseEnrollment.EnrollmentType.PURCHASED);
    }

    /**
     * Unenroll a user from a course.
     */
    @Transactional
    public void unenroll(Long userId, Long courseId) {
        if (!courseEnrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new BusinessRuleViolationException(ErrorCodes.USER_NOT_ENROLLED_TO_COURSE);
        }

        courseEnrollmentRepository.deleteByUserIdAndCourseId(userId, courseId);
        log.info("User {} unenrolled from course {}", userId, courseId);
    }

    /**
     * Update last accessed time (call when user views a lesson).
     */
    @Transactional
    public void updateLastAccessed(Long userId, Long courseId) {
        courseEnrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .ifPresent(enrollment -> {
                    enrollment.setLastAccessedAt(Instant.now());
                    courseEnrollmentRepository.save(enrollment);
                });
    }

    /**
     * Update progress percentage.
     */
    @Transactional
    public void updateProgress(Long userId, Long courseId, double progressPercent) {
        courseEnrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .ifPresent(enrollment -> {
                    enrollment.setProgressPercent(Math.min(100.0, Math.max(0.0, progressPercent)));
                    courseEnrollmentRepository.save(enrollment);
                });
    }

    private EnrolledCourseDTO toDTO(CourseEnrollment enrollment) {
        Course course = enrollment.getCourse();
        return EnrolledCourseDTO.builder()
                .courseId(course.getId())
                .courseName(course.getName())
                .courseImageUrl(course.getImageUrl())
                .enrolledAt(enrollment.getEnrolledAt())
                .progress(enrollment.getProgressPercent())
                .build();
    }

}
