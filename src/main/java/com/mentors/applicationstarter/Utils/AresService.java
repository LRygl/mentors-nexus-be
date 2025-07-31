package com.mentors.applicationstarter.Utils;

import com.mentors.applicationstarter.DTO.AresResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AresService {

    private final WebClient webClient;

    public AresService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://ares.gov.cz/ekonomicke-subjekty-v-be/rest").build();
    }

    public Mono<AresResponseDTO> getCompanyInfo(String vatNumber) {
        return webClient.get()
                .uri("/ekonomicke-subjekty/{vatNumber}",vatNumber)
                .header("accept","application/json")
                .retrieve()
                .bodyToMono(AresResponseDTO.class);
    }

}
