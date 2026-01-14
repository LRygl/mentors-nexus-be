package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.Category;
import com.mentors.applicationstarter.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    @Query("SELECT c FROM Course c JOIN c.sections s WHERE s.id = :sectionId")
    Course findByCourseSection(@Param("sectionId") Long sectionId);

    List<Course> findByFeaturedTrue();
    List<Course> findAllByCategoriesContaining(Category category);

}
