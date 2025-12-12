package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.LegalTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LegalTopicRepository extends JpaRepository<LegalTopic, Long> {

    @Query("SELECT t FROM LegalTopic t WHERE t.published = true")
    List<LegalTopic> findPublishedTopics();

    @Query("SELECT t FROM LegalTopic t WHERE t.id = :id AND t.published = true")
    LegalTopic findPublishedTopicById(@Param("id") Long id);
}
