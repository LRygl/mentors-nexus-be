package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.CourseDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseDiscountRepository extends JpaRepository<CourseDiscount, Long> {

    List<CourseDiscount> findByCourse(Course course);
}
