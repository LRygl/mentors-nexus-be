package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.LessonDTO;
import com.mentors.applicationstarter.DTO.LessonDetailDTO;
import com.mentors.applicationstarter.Model.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface LessonService {
    List<LessonDetailDTO> getAllLessons();

    LessonDTO getLessonById(Long lessonId);

    Page<Lesson> getPagedLessons(Pageable pageable);

    LessonDTO createLesson(Lesson lesson);

    LessonDTO updateLesson(Long lessonId, Lesson lesson, MultipartFile image);

    LessonDTO deleteLesson(Long lessonId);
}
