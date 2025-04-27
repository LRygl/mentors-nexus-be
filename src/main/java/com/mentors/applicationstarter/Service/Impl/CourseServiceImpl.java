package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Repository.CourseRepository;
import com.mentors.applicationstarter.Service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    public Course createCourse(Course request) {

        Course course = Course.builder()
                .created(Instant.now())
                .UUID(UUID.randomUUID())
                .name(request.getName())
                .published(Instant.parse(request.getPublished().toString()))
                .price(request.getPrice())
                .build();

        courseRepository.save(course);

        return course;
    }
}
