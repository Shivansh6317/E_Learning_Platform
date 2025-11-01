package com.example.auth.dto;
import lombok.Data;

@Data
public class CreateOrderRequest {
    private Long courseId;
    private double amount;
}

