package com.example.auth.service;

import com.example.auth.dto.PlagiarismRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlagiarismService {

    private final RestTemplate restTemplate;

    private static final String PLAGIARISM_API_URL = "https://plagiarism-detection-w1va.onrender.com/api/v1/detect";

    public Map<String, Object> detectPlagiarism(PlagiarismRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PlagiarismRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                PLAGIARISM_API_URL,
                HttpMethod.POST,
                entity,
                Map.class
        );

        return response.getBody();
    }
}
