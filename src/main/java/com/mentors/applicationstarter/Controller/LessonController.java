package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.DTO.LessonDTO;
import com.mentors.applicationstarter.Model.Lesson;
import com.mentors.applicationstarter.Service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lesson")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/all")
    public ResponseEntity<List<LessonDTO>> getAllLessons(){
        return new ResponseEntity<>(lessonService.getAllLessons(), HttpStatus.OK);
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable Long lessonId) {
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
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        return new ResponseEntity<>(lessonService.createLesson(lesson),HttpStatus.CREATED);
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<Lesson> updateLesson(@RequestBody Lesson request, @PathVariable Long lessonId) {
        return new ResponseEntity<>(lessonService.updateCompany(request,lessonId),HttpStatus.OK);
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Lesson> deleteLesson(@PathVariable Long lessonId) {
        return new ResponseEntity<>(lessonService.deleteLesson(lessonId),HttpStatus.GONE);
    }

}
