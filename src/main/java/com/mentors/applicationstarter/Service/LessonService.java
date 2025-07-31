package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.LessonDTO;
import com.mentors.applicationstarter.Model.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LessonService {
    List<LessonDTO> getAllLessons();

    Lesson getLessonById(Long lessonId);

    Page<Lesson> getPagedLessons(Pageable pageable);

    Lesson createLesson(Lesson lesson);

    Lesson updateCompany(Lesson request, Long lessonId);

    Lesson deleteLesson(Long lessonId);
}
