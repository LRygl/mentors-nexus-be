package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.Model.Lesson;
import com.mentors.applicationstarter.Service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lesson")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

@GetMapping("/all")
    public ResponseEntity<List<Lesson>> getAllLessons(){
    return new ResponseEntity<>(lessonService.getAllLessons(), HttpStatus.OK);
}





}
