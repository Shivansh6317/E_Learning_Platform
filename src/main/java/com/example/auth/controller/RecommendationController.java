package com.example.auth.controller;

import com.example.auth.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ml")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/recommend")
    public List<Map<String, Object>> getRecommendations(@RequestParam String course_name) {
        return recommendationService.getRecommendedCourses(course_name);
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchCourses(@RequestParam String keyword) {
        return recommendationService.searchCourses(keyword);
    }

    @GetMapping("/courses")
    public Map<String, Object> getCourses(@RequestParam(defaultValue = "10") int limit) {
        return recommendationService.getAllCourses(limit);
    }

}
