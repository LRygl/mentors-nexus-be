package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.DTO.CourseRequestDTO;
import com.mentors.applicationstarter.DTO.CourseResponseDTO;
import com.mentors.applicationstarter.DTO.CourseStatusDTO;
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

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        return new ResponseEntity<>(courseService.getCourseById(id), HttpStatus.FOUND);
    }

    @PostMapping
    public ResponseEntity<CourseResponseDTO> createNewCourse(@RequestBody CourseRequestDTO course) {
        return new ResponseEntity<>(courseService.createCourse(course), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<CourseResponseDTO> updateCourse(@RequestBody CourseRequestDTO courseRequestDTO) {
        return new ResponseEntity<>(courseService.updateCourse(courseRequestDTO), HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CourseResponseDTO> updateCourseStatus(@PathVariable Long id, @RequestBody CourseStatusDTO courseStatusDTO) {
        return new ResponseEntity<>(courseService.updateCourseStatus(id,courseStatusDTO),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> deleteCourse(@PathVariable Long id) {
        return new ResponseEntity<>(courseService.deleteCourse(id), HttpStatus.GONE);
    }
}
