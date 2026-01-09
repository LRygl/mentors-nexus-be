package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    Optional<CourseEnrollment> findByUserIdAndCourseId(Long userId, Long courseId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    List<CourseEnrollment> findByUserId(Long userId);

    List<CourseEnrollment> findByCourseId(Long courseId);

    @Query("SELECT e.course.id FROM CourseEnrollment e WHERE e.user.id = :userId")
    Set<Long> findCourseIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(e) FROM CourseEnrollment e WHERE e.course.id = :courseId")
    int countByCourseId(@Param("courseId") Long courseId);

    void deleteByUserIdAndCourseId(Long userId, Long courseId);
}
