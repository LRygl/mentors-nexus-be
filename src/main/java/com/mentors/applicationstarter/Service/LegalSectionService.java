package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.Response.Admin.LegalSectionAdminResponseDTO;
import com.mentors.applicationstarter.Model.LegalSection;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LegalSectionService {

    LegalSectionAdminResponseDTO createNewLegalSection(Long topicId, LegalSection section);
    
    List<LegalSectionAdminResponseDTO> getAllLegalSections();
    
    LegalSectionAdminResponseDTO getLegalSectionById(Long id);
    
    LegalSectionAdminResponseDTO updateLegalSection(Long id, LegalSection section);
    
    void deleteLegalSection(Long id);

    List<LegalSectionAdminResponseDTO> bulkReorderSections(Long topicId, List<Long> sectionIds);

    LegalSectionAdminResponseDTO moveSectionToPosition(Long sectionId, Integer position);
}
