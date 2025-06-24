package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.CategoryDTO;
import com.mentors.applicationstarter.Model.Category;

import java.util.stream.Collectors;

public class CategoryMapper {

    public static CategoryDTO toCategoryWithCoursesDto(Category category){
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .courses(category.getCourses().stream()
                        .map(CourseMapper::toSummaryDto)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
