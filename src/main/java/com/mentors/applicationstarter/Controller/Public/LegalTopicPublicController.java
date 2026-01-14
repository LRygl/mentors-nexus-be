package com.mentors.applicationstarter.Controller.Public;

import com.mentors.applicationstarter.DTO.Response.Public.LegalTopicPublicResponseDTO;
import com.mentors.applicationstarter.Service.LegalTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/legal/topic")
@RequiredArgsConstructor
public class LegalTopicPublicController {

    private final LegalTopicService legalTopicService;

    @GetMapping
    public ResponseEntity<List<LegalTopicPublicResponseDTO>> getAllPublicLegalTopic() {
        return new ResponseEntity<>(legalTopicService.getAllPublicLegalTopics(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LegalTopicPublicResponseDTO> getPublicLegalTopicById(@PathVariable Long id) {
        return new ResponseEntity<>(legalTopicService.getPublicLegalTopicById(id), HttpStatus.OK);
    }
}
