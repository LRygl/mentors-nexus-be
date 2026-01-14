package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.LegalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LegalItemRepository extends JpaRepository<LegalItem, Long> {

    // Fetch root items for a section ordered by orderIndex asc (nulls last)
    @Query("SELECT i FROM LegalItem i WHERE i.section.id = :sectionId AND i.parent IS NULL ORDER BY COALESCE(i.orderIndex, 2147483647)")
    List<LegalItem> findRootItemsBySectionId(@Param("sectionId") Long sectionId);

    // Fetch sub-items for a parent ordered by orderIndex asc
    @Query("SELECT i FROM LegalItem i WHERE i.parent.id = :parentId ORDER BY COALESCE(i.orderIndex, 2147483647)")
    List<LegalItem> findSubItemsByParentId(@Param("parentId") Long parentId);

    // Max orderIndex for root items in a section (nullable)
    @Query("SELECT MAX(i.orderIndex) FROM LegalItem i WHERE i.section.id = :sectionId AND i.parent IS NULL")
    Integer findMaxOrderIndexForRootItemsInSection(@Param("sectionId") Long sectionId);

    // Max orderIndex for sub-items of a parent (nullable)
    @Query("SELECT MAX(i.orderIndex) FROM LegalItem i WHERE i.parent.id = :parentId")
    Integer findMaxOrderIndexForSubItems(@Param("parentId") Long parentId);
}
