package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    List<CourseSection> findByCourseOrderByOrderIndexAsc(Course course);
    List<CourseSection> findByCourse(Course course);
}
