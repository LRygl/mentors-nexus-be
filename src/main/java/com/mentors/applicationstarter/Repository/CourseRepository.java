package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {


}
