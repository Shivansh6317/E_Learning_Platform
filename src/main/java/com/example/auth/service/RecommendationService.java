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


    public List<String> getRecommendedCourses(String courseName) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/recommend/")
                .queryParam("course_name", courseName)
                .toUriString();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> body = response.getBody();
        return (List<String>) body.get("similar_courses");
    }


    public List<String> searchCourses(String keyword) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/search/")
                .queryParam("keyword", keyword)
                .toUriString();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> body = response.getBody();
        return (List<String>) body.get("matching_courses");
    }


    public Map<String, Object> getAllCourses() {
        String url = BASE_URL + "/courses/";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return response.getBody();
    }
}
