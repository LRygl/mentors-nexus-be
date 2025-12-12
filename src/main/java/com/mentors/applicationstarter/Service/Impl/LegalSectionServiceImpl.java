package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.Response.Admin.LegalSectionAdminResponseDTO;
import com.mentors.applicationstarter.Mapper.LegalMapper;
import com.mentors.applicationstarter.Model.LegalSection;
import com.mentors.applicationstarter.Model.LegalTopic;
import com.mentors.applicationstarter.Repository.LegalSectionRepository;
import com.mentors.applicationstarter.Repository.LegalTopicRepository;
import com.mentors.applicationstarter.Service.LegalSectionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.mentors.applicationstarter.Utils.AuthUtils.getAuthenticatedUserUuid;

@Service
@RequiredArgsConstructor
public class LegalSectionServiceImpl implements LegalSectionService {

    private static final int INITIAL_ORDER_INDEX = 1;

    private final LegalTopicRepository legalTopicRepository;
    private final LegalSectionRepository legalSectionRepository;
    private final LegalMapper legalDtoMapper;

    @Override
    public LegalSectionAdminResponseDTO createNewLegalSection(Long topicId, LegalSection section) {
        LegalTopic topic = legalTopicRepository.findById(topicId).orElseThrow();

        if (section.getOrderIndex() == null) {
            Integer maxOrderIndex = legalSectionRepository.findMaxOrderIndexByTopicId(topicId);
            // Handle null case - if no sections exist yet, start at 1
            section.setOrderIndex(maxOrderIndex != null ? maxOrderIndex + 1 : 1);
        }


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
                .toList();
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

        boolean orderIndexChanged = false;
        Integer newOrderIndex = request.getOrderIndex();
        Integer oldOrderIndex = section.getOrderIndex();

        if (newOrderIndex != null && !Objects.equals(newOrderIndex, oldOrderIndex)) {
            section.setOrderIndex(newOrderIndex);
            orderIndexChanged = true;
        }

        legalSectionRepository.save(section);

        if (orderIndexChanged) {
            reorderSections(section.getTopic());
        }

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
    @Transactional
    public List<LegalSectionAdminResponseDTO> bulkReorderSections(Long topicId, List<Long> sectionIds) {
        // Verify topic exists
        if (!legalTopicRepository.existsById(topicId)) {
            throw new EntityNotFoundException("Legal Topic not found with id: " + topicId);
        }

        // Load all sections for the topic
        List<LegalSection> sections = legalSectionRepository.findByTopicIdOrderByOrderIndexAsc(topicId);

        if (sections.size() != sectionIds.size()) {
            throw new IllegalArgumentException(
                    String.format("Mismatch: provided %d sectionIds but topic has %d sections",
                            sectionIds.size(), sections.size())
            );
        }

        // Map for quick lookup
        Map<Long, LegalSection> sectionMap = sections.stream()
                .collect(Collectors.toMap(LegalSection::getId, s -> s));

        // Ensure all IDs belong to this topic
        for (Long id : sectionIds) {
            if (!sectionMap.containsKey(id)) {
                throw new IllegalArgumentException("Section ID " + id + " does not belong to topic " + topicId);
            }
        }

        // Reassign order indexes based on array order
        int index = INITIAL_ORDER_INDEX;
        for (Long id : sectionIds) {
            LegalSection section = sectionMap.get(id);
            section.setOrderIndex(index++);
            section.setUpdatedAt(Instant.now());
            section.setUpdatedBy(getAuthenticatedUserUuid());
        }

        legalSectionRepository.saveAll(sections);

        return sections.stream()
                .sorted(Comparator.comparing(LegalSection::getOrderIndex))
                .map(legalDtoMapper::toSectionDTO)
                .toList();
    }

    // PRIVATE METHODS
    @Transactional
    protected void reorderSections(LegalTopic topic) {
        List<LegalSection> sections = legalSectionRepository.findByTopicIdOrderByOrderIndexAsc(topic.getId());

        // Sort by orderIndex (stable sort handles duplicates)
        sections.sort(Comparator.comparing(LegalSection::getOrderIndex));

        // Reassign 1.n consistently
        int index = INITIAL_ORDER_INDEX;
        Instant now = Instant.now();
        UUID userUuid = getAuthenticatedUserUuid();

        for (LegalSection sec : sections) {
            sec.setOrderIndex(index++);
            sec.setUpdatedAt(now);
            sec.setUpdatedBy(userUuid);
        }

        legalSectionRepository.saveAll(sections);
    }
}
