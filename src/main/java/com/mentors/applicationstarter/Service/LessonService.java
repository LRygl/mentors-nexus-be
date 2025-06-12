package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.Model.Lesson;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LessonService {
    List<Lesson> getAllLessons();
}
