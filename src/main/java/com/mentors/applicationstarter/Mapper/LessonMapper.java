package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.LessonDTO;
import com.mentors.applicationstarter.Model.Lesson;

public class LessonMapper {

    public static LessonDTO toLessonDto(Lesson lesson) {
        return LessonDTO.builder()
                .id(lesson.getId())
                .uuid(lesson.getUUID())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .videoUrl(lesson.getVideoUrl())
                .duration(lesson.getLength())
                .courseName(lesson.getCourse() != null ? lesson.getCourse().getName() : null)
                .build();
    }
}
