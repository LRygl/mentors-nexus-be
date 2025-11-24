package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.LegalTopic;
import com.mentors.applicationstarter.Repository.LegalTopicRepository;
import com.mentors.applicationstarter.Service.LegalTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.mentors.applicationstarter.Utils.AuthUtils.getAuthenticatedUserUuid;

@Service
@RequiredArgsConstructor
public class LegalTopicServiceImpl implements LegalTopicService {

    private final LegalTopicRepository legalTopicRepository;

    @Override
    public List<LegalTopic> getAllLegalTopics() {
        return legalTopicRepository.findAll();
    }

    @Override
    public LegalTopic getLegalTopicById(Long id) {
        return findLegalTopicById(id);
    }

    @Override
    public LegalTopic createNewLegalTopic(LegalTopic request) {

        UUID topicUUID = UUID.randomUUID();
        UUID authenticatedUserUuid = getAuthenticatedUserUuid();

        LegalTopic topic = LegalTopic.builder()
                .uuid(topicUUID)
                .name(request.getName())
                .subtitle(request.getSubtitle())
                .effectiveAt(request.getEffectiveAt())
                .createdAt(Instant.now())
                .createdBy(authenticatedUserUuid)
                .showCta(request.getShowCta())
                .footer(request.getFooter())
                .build();

        return legalTopicRepository.save(topic);
    }

    @Override
    public LegalTopic updateLegalTopic(Long id, LegalTopic request) {

        LegalTopic topic = findLegalTopicById(id);
        UUID userUuid = getAuthenticatedUserUuid();

        topic.setUpdatedAt(Instant.now());
        topic.setUpdatedBy(userUuid);

        if(request.getName() != null) {
            topic.setName(request.getName());
        }
        if(request.getSubtitle() != null) {
            topic.setSubtitle(request.getSubtitle());
        }
        if(request.getShowCta() != null) {
            topic.setShowCta(request.getShowCta());
        }
        if(request.getEffectiveAt() != null) {
            topic.setEffectiveAt(request.getEffectiveAt());
        }

        legalTopicRepository.save(topic);
        return topic;
    }


    @Override
    public LegalTopic deleteLegalTopic(Long id) {
        LegalTopic topic = findLegalTopicById(id);
        legalTopicRepository.delete(topic);
        return topic;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private LegalTopic findLegalTopicById(Long topicId) {
        return legalTopicRepository.findById(topicId).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCodes.LEGAL_TOPIC_NOT_FOUND)
        );
    }


}
