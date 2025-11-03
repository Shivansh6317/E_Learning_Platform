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
    public List<String> getRecommendations(@RequestParam String course_name) {
        return recommendationService.getRecommendedCourses(course_name);
    }

    @GetMapping("/search")
    public List<String> searchCourses(@RequestParam String keyword) {
        return recommendationService.searchCourses(keyword);
    }

    @GetMapping("/courses")
    public Map<String, Object> getCourses() {
        return recommendationService.getAllCourses();
    }
}

