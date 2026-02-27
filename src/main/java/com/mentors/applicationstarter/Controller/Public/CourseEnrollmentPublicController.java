package com.mentors.applicationstarter.Controller.Public;

import com.mentors.applicationstarter.DTO.Course.EnrolledCourseDTO;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Service.CourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CourseEnrollmentPublicController {

    private final CourseEnrollmentService courseEnrollmentService;

    /**
     * Get all course IDs the current user is enrolled in.
     * Used for frontend caching.
     */
    @GetMapping("/user/enrollments/ids")
    public ResponseEntity<Set<Long>> getEnrolledCourseIds(
            @AuthenticationPrincipal User user
    ) {
        Set<Long> courseIds = courseEnrollmentService.getEnrolledCourseIds(user.getId());
        return ResponseEntity.ok(courseIds);
    }

    /**
     * Get detailed enrollment info for current user.
     */
    @GetMapping("/user/enrollments")
    public ResponseEntity<List<EnrolledCourseDTO>> getEnrollments(
            @AuthenticationPrincipal User user
    ) {
        List<EnrolledCourseDTO> enrollments = courseEnrollmentService.getUserEnrolledCourseIds(user.getId());
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Check if current user is enrolled in a specific course.
     */
    @GetMapping("/courses/{courseId}/enrollment-status")
    public ResponseEntity<Map<String, Boolean>> checkEnrollmentStatus(
            @AuthenticationPrincipal User user,
            @PathVariable Long courseId
    ) {
        boolean enrolled = courseEnrollmentService.isEnrolled(user.getId(), courseId);
        return ResponseEntity.ok(Map.of("enrolled", enrolled));
    }

}
