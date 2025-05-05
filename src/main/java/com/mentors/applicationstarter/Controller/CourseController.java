package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.DTO.CourseRequestDTO;
import com.mentors.applicationstarter.DTO.CourseResponseDTO;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;


@GetMapping
public ResponseEntity<List<CourseResponseDTO>> getAllCoursees(){
    return new ResponseEntity<>(courseService.getAllCourses(),HttpStatus.OK);
}

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long courseId) {
        return new ResponseEntity<>(courseService.getCourseById(courseId), HttpStatus.FOUND);
    }

    @PostMapping
    public ResponseEntity<CourseResponseDTO> createNewCourse(@RequestBody CourseRequestDTO course) {
        return new ResponseEntity<>(courseService.createCourse(course), HttpStatus.CREATED);
    }
}
