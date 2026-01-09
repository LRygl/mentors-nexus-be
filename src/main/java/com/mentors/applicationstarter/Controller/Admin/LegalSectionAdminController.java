package com.mentors.applicationstarter.Controller.Admin;

import com.mentors.applicationstarter.DTO.Response.Admin.LegalSectionAdminResponseDTO;
import com.mentors.applicationstarter.Model.LegalSection;
import com.mentors.applicationstarter.Service.LegalSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/legal/section")
@RequiredArgsConstructor
public class LegalSectionAdminController {

    private final LegalSectionService legalSectionService;

    @PostMapping("/topic/{topicId}")
    public ResponseEntity<LegalSectionAdminResponseDTO> createNewLegalSection(
            @PathVariable Long topicId,
            @RequestBody LegalSection section
    ) {
        return new ResponseEntity<>(legalSectionService.createNewLegalSection(topicId, section), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<LegalSectionAdminResponseDTO>> getAllLegalSections() {
        return new ResponseEntity<>(legalSectionService.getAllLegalSections(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LegalSectionAdminResponseDTO> getLegalSectionById(@PathVariable Long id) {
        return new ResponseEntity<>(legalSectionService.getLegalSectionById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LegalSectionAdminResponseDTO> updateLegalSection(
            @PathVariable Long id,
            @RequestBody LegalSection section
    ) {
        return new ResponseEntity<>(legalSectionService.updateLegalSection(id, section), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLegalSection(@PathVariable Long id) {
        legalSectionService.deleteLegalSection(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/topic/{topicId}/reorder")
    public ResponseEntity<List<LegalSectionAdminResponseDTO>> bulkReorderSections(
            @PathVariable Long topicId,
            @RequestBody List<Long> sectionIds
    ) {
        return new ResponseEntity<>(legalSectionService.bulkReorderSections(topicId, sectionIds), HttpStatus.OK);
    }

}
