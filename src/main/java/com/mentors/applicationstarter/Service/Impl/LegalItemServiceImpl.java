package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.Response.Admin.LegalItemAdminResponseDTO;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Mapper.LegalMapper;
import com.mentors.applicationstarter.Model.LegalItem;
import com.mentors.applicationstarter.Model.LegalSection;
import com.mentors.applicationstarter.Repository.LegalItemRepository;
import com.mentors.applicationstarter.Repository.LegalSectionRepository;
import com.mentors.applicationstarter.Service.LegalItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LegalItemServiceImpl implements LegalItemService {

    private final LegalItemRepository legalItemRepository;
    private final LegalSectionRepository legalSectionRepository;
    private final LegalMapper legalMapper;

    @Override
    public LegalItemAdminResponseDTO createNewLegalItem(Long sectionId, LegalItem item) {
        LegalSection section = legalSectionRepository.findById(sectionId)
                .orElseThrow();

        // Auto-set orderIndex if not provided
        if (item.getOrderIndex() == null) {
            Integer maxOrderIndex = section.getItems().stream()
                    .filter(i -> i.getParent() == null) // Only count root items
                    .map(LegalItem::getOrderIndex)
                    .filter(idx -> idx != null)
                    .max(Integer::compareTo)
                    .orElse(0);
            item.setOrderIndex(maxOrderIndex + 1);
        }

        item.setUuid(UUID.randomUUID());
        item.setSection(section);
        item.setCreatedAt(Instant.now());
        item.setParent(null); // Ensure it's a root item

        legalItemRepository.save(item);

        return legalMapper.toItemDTO(item);
    }

    @Override
    public LegalItemAdminResponseDTO createNewLegalSubItem(Long parentItemId, LegalItem item) {
        LegalItem parentItem = legalItemRepository.findById(parentItemId)
                .orElseThrow();

        // Auto-set orderIndex if not provided
        if (item.getOrderIndex() == null) {
            Integer maxOrderIndex = parentItem.getSubItems().stream()
                    .map(LegalItem::getOrderIndex)
                    .filter(idx -> idx != null)
                    .max(Integer::compareTo)
                    .orElse(0);
            item.setOrderIndex(maxOrderIndex + 1);
        }

        item.setUuid(UUID.randomUUID());
        item.setParent(parentItem);
        item.setSection(parentItem.getSection()); // Inherit section from parent
        item.setCreatedAt(Instant.now());

        legalItemRepository.save(item);

        return legalMapper.toItemDTO(item);
    }

    @Override
    public List<LegalItemAdminResponseDTO> getAllLegalItems() {
        return legalItemRepository.findAll()
                .stream()
                .map(legalMapper::toItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LegalItemAdminResponseDTO getLegalItemById(Long id) {
        LegalItem item = legalItemRepository.findById(id)
                .orElseThrow();
        return legalMapper.toItemDTO(item);
    }

    @Override
    @Transactional
    public LegalItemAdminResponseDTO updateLegalItem(Long id, LegalItem request) {
        LegalItem item = legalItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.LEGAL_ITEM_NOT_FOUND));

        item.setUpdatedAt(Instant.now());
        Optional.ofNullable(request.getContent()).ifPresent(item::setContent);

        Integer newIndex = request.getOrderIndex();
        Integer oldIndex = item.getOrderIndex();

        if (newIndex != null && !newIndex.equals(oldIndex)) {
            // Use reusable move logic – this saves the item
            moveItemToPosition(id, newIndex);

            // Refresh managed state
            item = legalItemRepository.findById(id).orElseThrow();
        } else if (newIndex != null) {
            // Explicitly set unchanged order index
            item.setOrderIndex(newIndex);
            // Explicit save for clarity
            legalItemRepository.save(item);
        }
        // else: newIndex is null → do not change orderIndex
        // JPA will auto-flush content/updatedAt change

        return legalMapper.toItemDTO(item);
    }

    @Transactional
    public List<LegalItemAdminResponseDTO> bulkReorderItems(Long sectionId, List<Long> itemIds) {
        // Fetch all root items in section and ensure ordering
        List<LegalItem> items = legalItemRepository.findRootItemsBySectionId(sectionId);
        // Map for quick lookup
        Map<Long, LegalItem> map = items.stream()
                .collect(Collectors.toMap(LegalItem::getId, it -> it));

        List<LegalItem> toSave = new ArrayList<>();
        for (int i = 0; i < itemIds.size(); i++) {
            Long id = itemIds.get(i);
            LegalItem it = map.get(id);
            if (it == null) {
                throw new IllegalArgumentException("Item " + id + " not found in section " + sectionId);
            }
            it.setOrderIndex(i + 1);
            it.setUpdatedAt(Instant.now());
            toSave.add(it);
        }
        legalItemRepository.saveAll(toSave);
        return toSave.stream().map(legalMapper::toItemDTO).collect(Collectors.toList());
    }
    
    @Transactional
    public LegalItemAdminResponseDTO moveItemToPosition(Long itemId, Integer newPosition) {
        if (newPosition == null || newPosition < 1) {
            throw new IllegalArgumentException("newPosition must be >= 1");
        }

        LegalItem item = legalItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.LEGAL_ITEM_NOT_FOUND));

        List<LegalItem> siblings;
        if (item.getParent() == null) {
            // root items in same section
            siblings = legalItemRepository.findRootItemsBySectionId(item.getSection().getId());
        } else {
            siblings = legalItemRepository.findSubItemsByParentId(item.getParent().getId());
        }

        // Remove the item from the list (if present)
        List<LegalItem> others = siblings.stream()
                .filter(i -> !i.getId().equals(itemId))
                .collect(Collectors.toList());

        int insertIndex = Math.min(others.size(), Math.max(0, newPosition - 1));
        others.add(insertIndex, item);

        // Renumber sequentially
        renumber(others);

        legalItemRepository.saveAll(others);
        return legalMapper.toItemDTO(item);
    }

    @Override
    @Transactional
    public void deleteLegalItem(Long id) {
        LegalItem item = legalItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.LEGAL_ITEM_NOT_FOUND));

        Long parentId = (item.getParent() == null ? null : item.getParent().getId());
        Long sectionId = item.getSection().getId();

        // Delete the item (cascade will remove children)
        legalItemRepository.delete(item);

        // Reorder siblings after deletion
        List<LegalItem> siblings;
        if (parentId == null) {
            siblings = legalItemRepository.findRootItemsBySectionId(sectionId);
        } else {
            siblings = legalItemRepository.findSubItemsByParentId(parentId);
        }

        renumber(siblings);
        legalItemRepository.saveAll(siblings);
    }

    /**
     * Reassign sequential orderIndex starting at 1 for the given list in the provided order.
     * Mutates items and sets updatedAt timestamp.
     */
    private void renumber(List<LegalItem> items) {
        for (int i = 0; i < items.size(); i++) {
            LegalItem it = items.get(i);
            int desired = i + 1;
            if (it.getOrderIndex() == null || !it.getOrderIndex().equals(desired)) {
                it.setOrderIndex(desired);
                it.setUpdatedAt(Instant.now());
            }
        }
    }
}
