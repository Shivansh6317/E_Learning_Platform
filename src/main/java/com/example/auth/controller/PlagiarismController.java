package com.example.auth.controller;

import com.example.auth.dto.PlagiarismRequest;
import com.example.auth.service.PlagiarismService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ml/plagiarism")
@RequiredArgsConstructor
public class PlagiarismController {

    private final PlagiarismService plagiarismService;

    @PostMapping("/detect")
    public Map<String, Object> detectPlagiarism(@RequestBody PlagiarismRequest request) {
        return plagiarismService.detectPlagiarism(request);
    }
}
