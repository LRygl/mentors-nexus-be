package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.Response.Admin.LegalTopicAdminResponseDTO;
import com.mentors.applicationstarter.DTO.Response.Public.LegalTopicPublicResponseDTO;
import com.mentors.applicationstarter.DTO.Response.Public.LegalTopicPublicSummaryResponseDTO;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Mapper.LegalMapper;
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
    private final LegalMapper legalDtoMapper;

    @Override
    public List<LegalTopicAdminResponseDTO> getAllLegalTopics() {
        return legalTopicRepository.findAll().stream()
                .map(legalDtoMapper::toTopicDTO)
                .toList();
    }

    @Override
    public LegalTopicAdminResponseDTO getLegalTopicById(Long id) {
        LegalTopic topic = findLegalTopicById(id);
        return legalDtoMapper.toTopicDTO(topic);
    }

    @Override
    public LegalTopicAdminResponseDTO createNewLegalTopic(LegalTopic request) {

        UUID topicUUID = UUID.randomUUID();

        LegalTopic topic = LegalTopic.builder()
                .uuid(topicUUID)
                .name(request.getName())
                .subtitle(request.getSubtitle())
                .effectiveAt(request.getEffectiveAt())
                .createdAt(Instant.now())
                .published(request.getPublished())
                .publishedAt(request.getPublishedAt())
                .showCta(request.getShowCta())
                .footer(request.getFooter())
                .build();
        legalTopicRepository.save(topic);
        return legalDtoMapper.toTopicDTO(topic);

    }

    @Override
    public LegalTopicAdminResponseDTO updateLegalTopic(Long id, LegalTopic request) {

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

        if(request.getPublished() != null) {
            topic.setPublished(true);
        }

        legalTopicRepository.save(topic);
        return legalDtoMapper.toTopicDTO(topic);
    }

    @Override
    public List<LegalTopicPublicResponseDTO> getAllPublicLegalTopics() {
        List<LegalTopic> publishedTopics = legalTopicRepository.findPublishedTopics();
        return publishedTopics.stream()
                .map(legalDtoMapper::toTopicPublicDTO)
                .toList();
    }

    @Override
    public LegalTopicPublicResponseDTO getPublicLegalTopicById(Long id) {
        LegalTopic topic = legalTopicRepository.findPublishedTopicById(id);
        return legalDtoMapper.toTopicPublicDTO(topic);
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
