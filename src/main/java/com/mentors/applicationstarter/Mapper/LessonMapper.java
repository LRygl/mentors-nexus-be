package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.CourseSummaryDTO;
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
                .course(lesson.getCourse() != null ? CourseSummaryDTO.builder()
                        .id(lesson.getCourse().getId())
                        .uuid(lesson.getCourse().getUuid())
                        .name(lesson.getCourse().getName())
                        .build() : null)
                .build();
    }
}
