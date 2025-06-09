package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.CourseResponseDTO;
import com.mentors.applicationstarter.DTO.CourseSummaryDTO;
import com.mentors.applicationstarter.DTO.UserResponseDTO;
import com.mentors.applicationstarter.Model.Category;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.Label;

import java.util.Set;
import java.util.stream.Collectors;

public class CourseMapper {

    public static CourseResponseDTO toDto(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .uuid(course.getUuid())
                .name(course.getName())
                .labels(course.getLabels().stream()
                        .map(Label::getName)
                        .collect(Collectors.toSet()))
                .categories(course.getCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.toSet()))
                .created(course.getCreated())
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
                .updated(course.getUpdated())
                .status(String.valueOf(course.getStatus()))
                .build();
    }

    public static CourseSummaryDTO toSummaryDto(Course course) {
        return CourseSummaryDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .status(course.getStatus() != null ? course.getStatus().name() : null)
                .uuid(course.getUuid())
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
}
