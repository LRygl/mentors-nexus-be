package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.Response.Admin.LegalItemAdminResponseDTO;
import com.mentors.applicationstarter.Model.LegalItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LegalItemService {

    LegalItemAdminResponseDTO createNewLegalItem(Long sectionId, LegalItem item);
    
    LegalItemAdminResponseDTO createNewLegalSubItem(Long parentItemId, LegalItem item);
    
    List<LegalItemAdminResponseDTO> getAllLegalItems();
    
    LegalItemAdminResponseDTO getLegalItemById(Long id);
    
    LegalItemAdminResponseDTO updateLegalItem(Long id, LegalItem item);
    
    void deleteLegalItem(Long id);
}
