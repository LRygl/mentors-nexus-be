package com.mentors.applicationstarter.Controller.Admin;

import com.mentors.applicationstarter.DTO.Response.Admin.LegalItemAdminResponseDTO;
import com.mentors.applicationstarter.Model.LegalItem;
import com.mentors.applicationstarter.Service.LegalItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/legal/item")
@RequiredArgsConstructor
public class LegalItemAdminController {

    private final LegalItemService legalItemService;

    @PostMapping("/section/{sectionId}")
    public ResponseEntity<LegalItemAdminResponseDTO> createNewLegalItem(
            @PathVariable Long sectionId,
            @RequestBody LegalItem item
    ) {
        return new ResponseEntity<>(legalItemService.createNewLegalItem(sectionId, item), HttpStatus.CREATED);
    }

    @PostMapping("/parent/{parentItemId}")
    public ResponseEntity<LegalItemAdminResponseDTO> createNewLegalSubItem(
            @PathVariable Long parentItemId,
            @RequestBody LegalItem item
    ) {
        return new ResponseEntity<>(legalItemService.createNewLegalSubItem(parentItemId, item), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LegalItemAdminResponseDTO>> getAllLegalItems() {
        return new ResponseEntity<>(legalItemService.getAllLegalItems(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LegalItemAdminResponseDTO> getLegalItemById(@PathVariable Long id) {
        return new ResponseEntity<>(legalItemService.getLegalItemById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LegalItemAdminResponseDTO> updateLegalItem(
            @PathVariable Long id,
            @RequestBody LegalItem item
    ) {
        return new ResponseEntity<>(legalItemService.updateLegalItem(id, item), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLegalItem(@PathVariable Long id) {
        legalItemService.deleteLegalItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
