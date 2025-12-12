package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.Response.Admin.LegalItemAdminResponseDTO;
import com.mentors.applicationstarter.DTO.Response.Admin.LegalSectionAdminResponseDTO;
import com.mentors.applicationstarter.DTO.Response.Admin.LegalTopicAdminResponseDTO;
import com.mentors.applicationstarter.DTO.Response.Public.LegalTopicPublicResponseDTO;
import com.mentors.applicationstarter.Model.LegalItem;
import com.mentors.applicationstarter.Model.LegalSection;
import com.mentors.applicationstarter.Model.LegalTopic;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class LegalMapper {
    public LegalTopicAdminResponseDTO toTopicDTO(LegalTopic topic) {
        LegalTopicAdminResponseDTO dto = new LegalTopicAdminResponseDTO();

        dto.setId(topic.getId());
        dto.setUuid(topic.getUuid());
        dto.setName(topic.getName());
        dto.setSubtitle(topic.getSubtitle());
        dto.setCreatedAt(topic.getCreatedAt());
        dto.setUpdatedAt(topic.getUpdatedAt());
        dto.setEffectiveAt(topic.getEffectiveAt());
        dto.setPublished(topic.getPublished());
        dto.setPublishedBy(topic.getPublishedBy());
        dto.setPublishedAt(topic.getPublishedAt());
        dto.setShowCta(topic.getShowCta());
        dto.setFooter(topic.getFooter());

        dto.setSections(
                topic.getSections() == null ? List.of() :
                topic.getSections().stream()
                        .sorted(Comparator.comparing(LegalSection::getOrderIndex, Comparator.nullsLast(Comparator.naturalOrder())))
                        .map(this::toSectionDTO)
                        .toList()
        );

        return dto;
    }

    public LegalSectionAdminResponseDTO toSectionDTO(LegalSection section) {
        LegalSectionAdminResponseDTO dto = new LegalSectionAdminResponseDTO();

        dto.setId(section.getId());
        dto.setUuid(section.getUuid());
        dto.setName(section.getName());
        dto.setIcon(section.getIcon());
        dto.setCreatedAt(section.getCreatedAt());
        dto.setUpdatedAt(section.getUpdatedAt());
        dto.setOrderIndex(section.getOrderIndex());

        dto.setItems(
                section.getItems() == null ? List.of() :
                section.getItems().stream()
                        .sorted(Comparator.comparing(LegalItem::getOrderIndex, Comparator.nullsLast(Comparator.naturalOrder())))
                        .map(this::toItemDTO)
                        .toList()
        );

        return dto;
    }

    public LegalItemAdminResponseDTO toItemDTO(LegalItem item) {
        if (item == null) return null;

        LegalItemAdminResponseDTO dto = new LegalItemAdminResponseDTO();

        dto.setId(item.getId());
        dto.setUuid(item.getUuid());
        dto.setContent(item.getContent());
        dto.setOrderIndex(item.getOrderIndex());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setCreatedBy(item.getCreatedBy());
        dto.setUpdatedAt(item.getUpdatedAt());
        dto.setUpdatedBy(item.getUpdatedBy());

        // recursive mapping of children - fully null safe
        List<LegalItem> children = item.getSubItems();
        dto.setSubItems(
                children == null
                        ? List.of()
                        : children.stream().map(this::toItemDTO).toList()
        );

        return dto;
    }

    public LegalTopicPublicResponseDTO toTopicPublicDTO(LegalTopic topic) {
        LegalTopicPublicResponseDTO dto = new LegalTopicPublicResponseDTO();

        dto.setId(topic.getId());
        dto.setUuid(topic.getUuid());
        dto.setName(topic.getName());
        dto.setSubtitle(topic.getSubtitle());
        dto.setCreatedAt(topic.getCreatedAt());
        dto.setUpdatedAt(topic.getUpdatedAt());
        dto.setCreatedBy(topic.getCreatedBy());
        dto.setUpdatedBy(topic.getUpdatedBy());
        dto.setPublished(topic.getPublished());
        dto.setPublishedAt(topic.getPublishedAt());
        dto.setPublishedBy(topic.getPublishedBy());
        dto.setEffectiveAt(topic.getEffectiveAt());
        dto.setShowCta(topic.getShowCta());
        dto.setFooter(topic.getFooter());

        dto.setSections(
                topic.getSections() == null ? List.of() :
                        topic.getSections().stream()
                                .sorted(Comparator.comparing(LegalSection::getOrderIndex, Comparator.nullsLast(Comparator.naturalOrder())))
                                .map(this::toSectionDTO)
                                .toList()
        );

        return dto;
    }


}
