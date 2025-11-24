package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.Model.LegalTopic;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LegalTopicService {
    LegalTopic createNewLegalTopic(LegalTopic request);
    LegalTopic getLegalTopicById(Long id);

    List<LegalTopic> getAllLegalTopics();

    LegalTopic deleteLegalTopic(Long id);

    LegalTopic updateLegalTopic(Long id, LegalTopic request);
}
