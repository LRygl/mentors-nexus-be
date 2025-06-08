package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.CourseResponseDTO;
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
                .created(course.getCreated())
                .published(course.getPublished())
                .status(course.getStatus() != null ? course.getStatus().name() : null)
                .name(course.getName())
                .labels(mapLabels(course.getLabels()))
                .categories(mapCategories(course.getCategories()))
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
