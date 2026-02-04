package com.mentors.applicationstarter.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentors.applicationstarter.DTO.CourseRequestDTO;
import com.mentors.applicationstarter.DTO.CourseResponseDTO;
import com.mentors.applicationstarter.DTO.CourseStatusDTO;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.CourseSection;
import com.mentors.applicationstarter.Service.CourseService;
import com.mentors.applicationstarter.Service.Impl.CourseServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.DataInput;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;
    private final ObjectMapper objectMapper;


    @GetMapping("/all")
    public ResponseEntity<List<CourseResponseDTO>> getAllCoursees(){
    return new ResponseEntity<>(courseService.getAllCourses(),HttpStatus.OK);
    }


    @GetMapping("/featured")
    public ResponseEntity<List<CourseResponseDTO>> getAllFeaturedCourses() {
        return new ResponseEntity<>(courseService.getAllFeaturedCourses(), HttpStatus.OK);
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
        return new ResponseEntity<>(courseService.getCourseById(id), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseResponseDTO> createNewCourseMultipart(
            @RequestPart(value = "course") CourseRequestDTO courseRequestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return new ResponseEntity<>(courseService.createCourse(courseRequestDTO, image), HttpStatus.CREATED);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CourseResponseDTO> createNewCourseMultipart(
            @RequestBody CourseRequestDTO courseRequestDTO
    ) {
        return new ResponseEntity<>(courseService.createCourse(courseRequestDTO, null), HttpStatus.CREATED);
    }

    @PostMapping("/{courseId}/section")
    public ResponseEntity<CourseResponseDTO> createNewCourseSection(
            @RequestBody CourseSection section,
            @PathVariable Long courseId) {
        return new ResponseEntity<>(courseService.createCourseSection(section, courseId), HttpStatus.CREATED);
    }

    @PostMapping("/section/{sectionId}/lesson/{lessonId}")
    public ResponseEntity<CourseResponseDTO> addLessonToCourseSection(@PathVariable Long sectionId, @PathVariable Long lessonId) {
        return new ResponseEntity<>(courseService.addLessonToCourseSection(sectionId,lessonId),HttpStatus.OK);
    }

    @DeleteMapping("/section/{sectionId}/lesson/{lessonId}")
    public ResponseEntity<CourseResponseDTO> removeLessonFromCourseSection(
            @PathVariable Long sectionId,
            @PathVariable Long lessonId
    ) {
        return new ResponseEntity<>(courseService.removeLessonFromCourse(sectionId,lessonId), HttpStatus.OK);
    }

    @PostMapping("/section/reorder")
    public ResponseEntity<CourseResponseDTO> reorderCourseSection(@RequestBody List<Long> sectionOrder) {
        return new ResponseEntity<>(courseService.reorderCourseSections(sectionOrder), HttpStatus.OK);
    }

    @PutMapping(value = "/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseResponseDTO> updateCourseMultipart(
            @PathVariable Long courseId,
            @RequestPart(value = "course") CourseRequestDTO courseRequestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image
            ) throws IOException {

        if (image != null) {
            log.info("Received file: name={}, originalFilename={}, contentType={}, size={}",
                    image.getName(),
                    image.getOriginalFilename(),
                    image.getContentType(),
                    image.getSize()
            );
        }
        return new ResponseEntity<>(courseService.updateCourse(courseId, courseRequestDTO, image), HttpStatus.OK);
    }

    @PutMapping(value = "/{courseId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Long courseId,
            @RequestBody CourseRequestDTO course
    ) throws IOException {

        log.info("Updating course with id {}", courseId);
        return new ResponseEntity<>(courseService.updateCourse(courseId, course, null), HttpStatus.OK);
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<CourseResponseDTO> updateCourseStatus(@PathVariable Long id, @RequestBody CourseStatusDTO courseStatusDTO) {
        return new ResponseEntity<>(courseService.updateCourseStatus(id,courseStatusDTO),HttpStatus.OK);
    }

    @PatchMapping("/{id}/feature")
    public ResponseEntity<CourseResponseDTO> featureCourse(@PathVariable Long id) {
        return new ResponseEntity<>(courseService.featureCourse(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}/unfeature")
    public ResponseEntity<CourseResponseDTO> unfeatureCourse(@PathVariable Long id) {
        return new ResponseEntity<>(courseService.unfeatureCourse(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> deleteCourse(@PathVariable Long id) {
        return new ResponseEntity<>(courseService.deleteCourse(id), HttpStatus.OK);
    }

    @DeleteMapping("/section/{id}")
    public ResponseEntity<CourseResponseDTO> deleteCourseSection(@PathVariable Long id) {
        return new ResponseEntity<>(courseService.delteCourseSection(id), HttpStatus.OK);
    }
}
