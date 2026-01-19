package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    /**
     * Find lesson by UUID
     * Similar to findById but using the UUID field instead of ID
     */
    Optional<Lesson> findByUuid(UUID uuid);
}
