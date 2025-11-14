package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.*;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.CourseSection;
import com.mentors.applicationstarter.Model.Lesson;

public class LessonMapper {

    public static LessonDTO toLessonDto(Lesson lesson) {
        return LessonDTO.builder()
                .id(lesson.getId())
                .uuid(lesson.getUuid())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .imageUrl(lesson.getImageUrl())
                .videoUrl(lesson.getVideoUrl())
                .duration(lesson.getDuration())
                .createdAt(lesson.getCreatedAt())
                .createdBy(lesson.getCreatedBy())
                .updatedAt(lesson.getUpdatedAt())
                .updatedBy(lesson.getUpdatedBy())
                .build();
    }

    public static LessonDetailDTO toDetailDTO(Lesson lesson) {

        if (lesson == null) return null;

        CourseSection section = lesson.getSection();
        Course course = section != null ? section.getCourse() : null;

        return LessonDetailDTO.builder()
                .id(lesson.getId())
                .uuid(lesson.getUuid())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .imageUrl(lesson.getImageUrl())
                .videoUrl(lesson.getVideoUrl())
                .duration(lesson.getDuration())
                .orderIndex(lesson.getOrderIndex())
                .createdAt(lesson.getCreatedAt())
                .createdBy(lesson.getCreatedBy())
                .updatedAt(lesson.getUpdatedAt())
                .updatedBy(lesson.getUpdatedBy())
                .section(section == null ? null : CourseSectionSummaryDTO.builder()
                        .id(section.getId())
                        .uuid(section.getUuid())
                        .title(section.getTitle())
                        .description(section.getDescription())
                        .build())
                .course(course == null ? null : CourseSummaryDTO.builder()
                        .id(course.getId())
                        .uuid(course.getUuid())
                        .name(course.getName())
                        .owner(course.getOwner() == null ? null : UserSummaryDTO.builder()
                                .id(course.getOwner().getId())
                                .firstName(course.getOwner().getFirstName())
                                .lastName(course.getOwner().getLastName())
                                .email(course.getOwner().getEmail())
                                .build())
                        .build())
                .build();
    }

}
