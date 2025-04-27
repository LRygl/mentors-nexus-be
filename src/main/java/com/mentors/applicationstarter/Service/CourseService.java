package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.Model.Course;
import org.springframework.stereotype.Service;

@Service
public interface CourseService {
    Course createCourse(Course course);
}
