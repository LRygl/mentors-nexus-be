package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.Response.Admin.LegalSectionAdminResponseDTO;
import com.mentors.applicationstarter.Mapper.LegalMapper;
import com.mentors.applicationstarter.Model.LegalSection;
import com.mentors.applicationstarter.Model.LegalTopic;
import com.mentors.applicationstarter.Repository.LegalSectionRepository;
import com.mentors.applicationstarter.Repository.LegalTopicRepository;
import com.mentors.applicationstarter.Service.LegalSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.mentors.applicationstarter.Utils.AuthUtils.getAuthenticatedUserUuid;

@Service
@RequiredArgsConstructor
public class LegalSectionServiceImpl implements LegalSectionService {

    private final LegalTopicRepository legalTopicRepository;
    private final LegalSectionRepository legalSectionRepository;
    private final LegalMapper legalDtoMapper;

    @Override
    public LegalSectionAdminResponseDTO createNewLegalSection(Long topicId, LegalSection section) {
        LegalTopic topic = legalTopicRepository.findById(topicId).orElseThrow();

        section.setUuid(UUID.randomUUID());
        section.setTopic(topic);
        section.setCreatedAt(Instant.now());

        legalSectionRepository.save(section);

        return legalDtoMapper.toSectionDTO(section);
    }

    @Override
    public List<LegalSectionAdminResponseDTO> getAllLegalSections() {
        return legalSectionRepository.findAll()
                .stream()
                .map(legalDtoMapper::toSectionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LegalSectionAdminResponseDTO getLegalSectionById(Long id) {
        LegalSection section = legalSectionRepository.findById(id)
                .orElseThrow();
        return legalDtoMapper.toSectionDTO(section);
    }

    @Override
    @Transactional
    public LegalSectionAdminResponseDTO updateLegalSection(Long id, LegalSection request) {
        LegalSection section = legalSectionRepository.findById(id)
                .orElseThrow();

        UUID userUuid = getAuthenticatedUserUuid();
        section.setUpdatedAt(Instant.now());
        section.setUpdatedBy(userUuid);

        if (request.getName() != null) {
            section.setName(request.getName());
        }
        if (request.getIcon() != null) {
            section.setIcon(request.getIcon());
        }
        if (request.getOrderIndex() != null) {
            section.setOrderIndex(request.getOrderIndex());
        }

        legalSectionRepository.save(section);

        return legalDtoMapper.toSectionDTO(section);
    }

    @Override
    @Transactional
    public void deleteLegalSection(Long id) {
        LegalSection section = legalSectionRepository.findById(id)
                .orElseThrow();
        
        // Due to cascade = CascadeType.ALL and orphanRemoval = true on the items relationship,
        // all child LegalItems will be automatically deleted when the section is deleted
        legalSectionRepository.delete(section);
    }

    @Override
    public List<LegalSectionAdminResponseDTO> bulkReorderSections(Long topicId, List<Long> sectionIds) {
        return List.of();
    }

    @Override
    public LegalSectionAdminResponseDTO moveSectionToPosition(Long sectionId, Integer position) {
        return null;
    }
}
