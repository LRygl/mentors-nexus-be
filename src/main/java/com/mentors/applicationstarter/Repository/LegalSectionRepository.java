package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.LegalSection;
import org.apache.el.stream.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LegalSectionRepository extends JpaRepository<LegalSection, Long> {
    List<LegalSection> findByTopicIdOrderByOrderIndexAsc(Long id);

    @Query("SELECT MAX(s.orderIndex) FROM LegalSection s WHERE s.topic.id = :topicId")
    Integer findMaxOrderIndexByTopicId(@Param("topicId") Long topicId);
}
