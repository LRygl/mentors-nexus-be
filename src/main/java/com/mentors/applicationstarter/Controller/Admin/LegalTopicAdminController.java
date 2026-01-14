package com.mentors.applicationstarter.Controller.Admin;

import com.mentors.applicationstarter.DTO.Response.Admin.LegalTopicAdminResponseDTO;
import com.mentors.applicationstarter.Model.LegalTopic;
import com.mentors.applicationstarter.Service.LegalTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/legal/topic")
@RequiredArgsConstructor
public class LegalTopicAdminController {

    private final LegalTopicService legalTopicService;

    @GetMapping("/all")
    public ResponseEntity<List<LegalTopicAdminResponseDTO>> getAllLegalTopic() {
        return new ResponseEntity<>(legalTopicService.getAllLegalTopics(),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LegalTopicAdminResponseDTO> getLegalTopicById(@PathVariable Long id) {
        return new ResponseEntity<>(legalTopicService.getLegalTopicById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LegalTopicAdminResponseDTO> createNewLegalTopic(@RequestBody LegalTopic request) {
        return new ResponseEntity<>(legalTopicService.createNewLegalTopic(request), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LegalTopicAdminResponseDTO> updateLegalTopic(@PathVariable Long id, @RequestBody LegalTopic request) {
        return new ResponseEntity<>(legalTopicService.updateLegalTopic(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LegalTopic> deleteLegalTopic(@PathVariable Long id) {
        return new ResponseEntity<>(legalTopicService.deleteLegalTopic(id),HttpStatus.OK);
    }
}
