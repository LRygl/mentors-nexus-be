package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.*;
import com.mentors.applicationstarter.Model.*;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CourseMapper {

    public static CourseResponseDTO toDto(Course course) {

        int totalDuration = course.getSections() == null ? 0 :
                course.getSections().stream()
                        .filter(section -> section.getLessons() != null)
                        .flatMap(section -> section.getLessons().stream())
                        .mapToInt(Lesson::getDuration)
                        .sum();


        return CourseResponseDTO.builder()
                .id(course.getId())
                .uuid(course.getUuid())
                .name(course.getName())
                .description(course.getDescription())
                .labels(course.getLabels().stream()
                        .map(Label::getName)
                        .collect(Collectors.toSet()))
                .categoryIds(course.getCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet()))
                .owner(
                        course.getOwner() == null ? null :
                                UserResponseDTO.builder()
                                        .id(course.getOwner().getId())
                                        .firstName(course.getOwner().getFirstName())
                                        .lastName(course.getOwner().getLastName())
                                        .email(course.getOwner().getEmail())
                                        .build()
                )
                .published(course.getPublished())
                .duration(course.getTotalDuration())
                .featured(course.getFeatured())
                .imageUrl(course.getImageUrl())
                .createdBy(course.getCreatedBy())
                .createdAt(course.getCreatedAt())
                .updatedBy(course.getUpdatedBy())
                .updatedAt(course.getUpdatedAt())
                .status(String.valueOf(course.getStatus()))
                .price(course.getPrice())
                .sections(course.getSections() == null ? null :
                        course.getSections().stream()
                                .map(section -> CourseSectionDTO.builder()
                                        .id(section.getId())
                                        .uuid(section.getUuid())
                                        .title(section.getTitle())
                                        .description(section.getDescription())
                                        .orderIndex(section.getOrderIndex())
                                        .lessons(section.getLessons() == null ? null :
                                                section.getLessons().stream()
                                                        .map(lesson -> LessonDTO.builder()
                                                                .id(lesson.getId())
                                                                .uuid(lesson.getUuid())
                                                                .title(lesson.getTitle())
                                                                .description(lesson.getDescription())
                                                                .videoUrl(lesson.getVideoUrl())
                                                                .duration(lesson.getDuration())
                                                                .orderIndex(lesson.getOrderIndex())
                                                                .build())
                                                        .collect(Collectors.toList())
                                        )
                                        .build())
                                .collect(Collectors.toList())
                )
                .students(course.getStudents().size())
                .goals(
                        course.getGoals().stream()
                                .sorted(Comparator.comparingInt(CourseGoals::getPosition))
                                .map(CourseGoals::getDescription)
                                .collect(Collectors.toList())
                )
                .requirements(
                        course.getRequirements().stream()
                                .sorted(Comparator.comparingInt(CourseRequirement::getPosition))
                                .map(CourseRequirement::getDescription)
                                .collect(Collectors.toList())
                )

                .build();

    }

    public static CourseSummaryDTO toSummaryDto(Course course) {
        return CourseSummaryDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .status(course.getStatus() != null ? course.getStatus().name() : null)
                .uuid(course.getUuid())
                .price(course.getPrice())
                .build();
    }

    private static Set<String> mapLabels(Set<Label> labels) {
        return labels == null ? Set.of() :
                labels.stream()
                        .map(Label::getName) // assuming `Label` has a `getName()` method
                        .collect(Collectors.toSet());
    }

    private static Set<String> mapCategories(Set<Category> categories) {
        return categories == null ? Set.of() :
                categories.stream()
                        .map(Category::getName) // assuming `Category` has a `getName()` method
                        .collect(Collectors.toSet());
    }


    // PRIVATE HELPER METHODS


    private static List<CourseSectionDTO> toSectionDtoList(List<CourseSection> sections) {
        if (sections == null) return null;
        return sections.stream().map(CourseMapper::toSectionDto).toList();
    }

    private static CourseSectionDTO toSectionDto(CourseSection section) {
        return CourseSectionDTO.builder()
                .id(section.getId())
                .uuid(section.getUuid())
                .title(section.getTitle())
                .description(section.getDescription())
                .orderIndex(section.getOrderIndex())
                .lessons(toLessonDtoList(section.getLessons()))
                .build();
    }

    private static List<LessonDTO> toLessonDtoList(List<Lesson> lessons) {
        if (lessons == null) return null;
        return lessons.stream().map(CourseMapper::toLessonDto).toList();
    }

    private static LessonDTO toLessonDto(Lesson lesson) {
        return LessonDTO.builder()
                .id(lesson.getId())
                .uuid(lesson.getUuid())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .videoUrl(lesson.getVideoUrl())
                .duration(lesson.getDuration())
                .orderIndex(lesson.getOrderIndex())
                .build();
    }


}



