package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.CategoryDTO;
import com.mentors.applicationstarter.Model.Category;

import java.util.stream.Collectors;

public class CategoryMapper {

    public static CategoryDTO toCategoryWithCoursesDto(Category category){
        return CategoryDTO.builder()
                .id(category.getId())
                .uuid(category.getUuid())
                .name(category.getName())
                .description(category.getDescription())
                .createdBy(category.getCreatedBy())
                .updatedBy(category.getUpdatedBy())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .color(category.getColor())
                .courses(category.getCourses().stream()
                        .map(CourseMapper::toSummaryDto)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
