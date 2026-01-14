package com.mentors.applicationstarter.Controller.Admin;

import com.mentors.applicationstarter.DTO.Course.EnrolledCourseDTO;
import com.mentors.applicationstarter.Service.CourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/enrollments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class CourseEnrollmentAdminController {


    private final CourseEnrollmentService courseEnrollmentService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<EnrolledCourseDTO>> getEnrollments(@PathVariable Long userId) {
        return new ResponseEntity<>(courseEnrollmentService.getEnrollments(userId), HttpStatus.OK);
    }


}
