package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.DTO.CourseRequestDTO;
import com.mentors.applicationstarter.DTO.CourseResponseDTO;
import com.mentors.applicationstarter.DTO.CourseStatusDTO;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;


    @GetMapping("/all")
    public ResponseEntity<List<CourseResponseDTO>> getAllCoursees(){
    return new ResponseEntity<>(courseService.getAllCourses(),HttpStatus.OK);
    }

    @GetMapping
    public Page<CourseResponseDTO> getPageCourses(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Set<String> categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page,size,Sort.by(direction, sort[0]));
        return courseService.getPagedCourses(name,categoryName ,pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        return new ResponseEntity<>(courseService.getCourseById(id), HttpStatus.FOUND);
    }

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<Void> enrollCourse(@PathVariable Long courseId, @RequestParam UUID userUUID) {
        courseService.enrollUserToCourse(courseId,userUUID);
        return null;
    }

    @PostMapping
    public ResponseEntity<CourseResponseDTO> createNewCourse(@RequestBody CourseRequestDTO course) {
        return new ResponseEntity<>(courseService.createCourse(course), HttpStatus.CREATED);
    }

    @PostMapping("/{courseId}/lesson/{lessonId}")
    public ResponseEntity<CourseResponseDTO> addLesson(@PathVariable Long courseId, @PathVariable Long lessonId) {
        return new ResponseEntity<>(courseService.addLessonToCourse(courseId,lessonId),HttpStatus.OK);
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
