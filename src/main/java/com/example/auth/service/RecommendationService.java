package com.example.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "https://student-recomdentation-system.onrender.com";

    public List<Map<String, Object>> getRecommendedCourses(String courseName) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/recommend")
                .queryParam("course_name", courseName)
                .toUriString();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> body = response.getBody();

        return (List<Map<String, Object>>) body.get("recommended_courses");
    }

    public List<Map<String, Object>> searchCourses(String keyword) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/search")
                .queryParam("keyword", keyword)
                .toUriString();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> body = response.getBody();

        if (body == null) {
            throw new RuntimeException("Empty response from ML API");
        }

        if (body.containsKey("message")) {
            throw new RuntimeException("No results: " + body.get("message"));
        }

        return (List<Map<String, Object>>) body.get("courses");
    }


    public Map<String, Object> getAllCourses(int limit) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/courses/")
                .queryParam("limit", limit)
                .toUriString();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return response.getBody();
    }

}
