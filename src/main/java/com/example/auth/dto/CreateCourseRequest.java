package com.example.auth.dto;

import lombok.Data;

@Data
public class CreateCourseRequest {
    private String title;
    private String description;
    private String category;
    private Double price;
}
