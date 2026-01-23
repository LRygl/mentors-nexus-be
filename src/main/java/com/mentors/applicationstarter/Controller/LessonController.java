package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.DTO.LessonDTO;
import com.mentors.applicationstarter.DTO.LessonDetailDTO;
import com.mentors.applicationstarter.Model.Lesson;
import com.mentors.applicationstarter.Service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lesson")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/all")
    public ResponseEntity<List<LessonDetailDTO>> getAllLessons(){
        return new ResponseEntity<>(lessonService.getAllLessons(), HttpStatus.OK);
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonDTO> getLessonById(@PathVariable Long lessonId) {
        return new ResponseEntity<>(lessonService.getLessonById(lessonId), HttpStatus.OK);
    }

    @GetMapping
    public Page<Lesson> getPagedLessons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page,size,Sort.by(direction,sort[0]));
        return lessonService.getPagedLessons(pageable);
    }

    @PostMapping
    public ResponseEntity<LessonDTO> createLesson(@RequestBody Lesson lesson) {
        return new ResponseEntity<>(lessonService.createLesson(lesson),HttpStatus.CREATED);
    }

    @PutMapping(value = "/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LessonDTO> updateLessonMultipart(
            @PathVariable Long lessonId,
            @RequestPart(value="lesson") Lesson lesson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "video", required = false) MultipartFile video
            ) {
        return new ResponseEntity<>(lessonService.updateLesson(lessonId, lesson, image, video),HttpStatus.OK);
    }

    @PutMapping(value = "/{lessonId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LessonDTO> updateLessonJson(
            @PathVariable Long lessonId,
            @RequestBody Lesson lesson
    ) {
        return new ResponseEntity<>(lessonService.updateLesson(lessonId, lesson, null, null),HttpStatus.OK);
    }


    @DeleteMapping("/{lessonId}")
    public ResponseEntity<LessonDTO> deleteLesson(@PathVariable Long lessonId) {
        return new ResponseEntity<>(lessonService.deleteLesson(lessonId),HttpStatus.OK);
    }

}
