package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Model.Lesson;
import com.mentors.applicationstarter.Repository.LessonRepository;
import com.mentors.applicationstarter.Service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    @Override
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }
}
