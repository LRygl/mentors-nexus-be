package com.mentors.applicationstarter.Controller.Admin;

import com.mentors.applicationstarter.DTO.Course.EnrolledCourseDTO;
import com.mentors.applicationstarter.Model.CourseEnrollment;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Service.CourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/enrollments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class CourseEnrollmentAdminController {


    private final CourseEnrollmentService courseEnrollmentService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<EnrolledCourseDTO>> getUserEnrolledCourseIds(@PathVariable Long userId) {
        return new ResponseEntity<>(courseEnrollmentService.getUserEnrolledCourseIds(userId), HttpStatus.OK);
    }


    /**
     * Enroll any user in a course.
     * POST /api/v1/admin/enrollments/users/{userId}/courses/{courseId}
     */
    @PostMapping("/users/{userId}/courses/{courseId}")
    public ResponseEntity<EnrolledCourseDTO> enrollUserToCourse(
            @PathVariable Long userId,
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "ADMIN_ASSIGNED") CourseEnrollment.EnrollmentType type
    ) {
        EnrolledCourseDTO enrollment = courseEnrollmentService.enrollByAdmin(userId, courseId, type);
        return ResponseEntity.ok(enrollment);
    }

    /**
     * Unenroll any user from a course.
     * DELETE /api/v1/admin/enrollments/users/{userId}/courses/{courseId}
     */
    @DeleteMapping("/users/{userId}/courses/{courseId}")
    public ResponseEntity<Void> unenrollUserFromCourse(
            @PathVariable Long userId,
            @PathVariable Long courseId
    ) {
        courseEnrollmentService.unenroll(userId, courseId);
        return ResponseEntity.noContent().build();
    }



}
