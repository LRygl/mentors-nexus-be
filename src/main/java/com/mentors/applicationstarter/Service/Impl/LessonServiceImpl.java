package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.LessonDTO;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Mapper.LessonMapper;
import com.mentors.applicationstarter.Model.Lesson;
import com.mentors.applicationstarter.Repository.LessonRepository;
import com.mentors.applicationstarter.Service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    @Override
    public List<LessonDTO> getAllLessons() {
        return lessonRepository.findAll().stream()
                .map(LessonMapper::toLessonDto)
                .collect(Collectors.toList());
    }

    @Override
    public Lesson getLessonById(Long lessonId) {
        return findLessonById(lessonId);
    }

    @Override
    public Page<Lesson> getPagedLessons(Pageable pageable) {
        Page<Lesson> lessonPage = lessonRepository.findAll(pageable);
        return lessonPage;
    }

    @Override
    public Lesson createLesson(Lesson request) {
        Lesson lesson = Lesson.builder()
                .UUID(UUID.randomUUID())
                .title(request.getTitle())
                .description(request.getDescription())
                .orderIndex(request.getOrderIndex())
                .videoUrl(request.getVideoUrl())
                .length(request.getLength())
                .createdDate(Instant.now())
                .build();

        lessonRepository.save(lesson);
        return lesson;
    }

    @Override
    public Lesson updateCompany(Lesson request, Long lessonId) {
        Lesson lesson = findLessonById(lessonId);

        lesson.setUpdatedDate(Instant.now());
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setLength(request.getLength());
        lesson.setVideoUrl(request.getVideoUrl());

        lessonRepository.save(lesson);
        return lesson;
    }

    @Override
    public Lesson deleteLesson(Long lessonId) {
        Lesson lesson = findLessonById(lessonId);
        lessonRepository.delete(lesson);
        return lesson;
    }

    private Lesson findLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId).orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.LESSON_NOT_FOUND));
    }
}
