package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.Response.Admin.LegalTopicAdminResponseDTO;
import com.mentors.applicationstarter.DTO.Response.Public.LegalTopicPublicResponseDTO;
import com.mentors.applicationstarter.DTO.Response.Public.LegalTopicPublicSummaryResponseDTO;
import com.mentors.applicationstarter.Model.LegalTopic;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LegalTopicService {
    LegalTopicAdminResponseDTO createNewLegalTopic(LegalTopic request);
    LegalTopicAdminResponseDTO getLegalTopicById(Long id);

    List<LegalTopicAdminResponseDTO> getAllLegalTopics();

    LegalTopic deleteLegalTopic(Long id);

    LegalTopicAdminResponseDTO updateLegalTopic(Long id, LegalTopic request);

    List<LegalTopicPublicResponseDTO> getAllPublicLegalTopics();

    LegalTopicPublicResponseDTO getPublicLegalTopicById(Long id);
}
