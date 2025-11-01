package com.example.auth.controller;

import com.example.auth.dto.CreateOrderRequest;
import com.example.auth.dto.VerifyPaymentRequest;
import com.example.auth.service.PaymentService;
import com.example.auth.config.JwtProvider;
import com.example.auth.exception.CustomException;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtProvider jwtProvider;

    // ✅ CREATE ORDER
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request, HttpServletRequest httpRequest)
            throws RazorpayException {
        String email = extractEmailFromToken(httpRequest);
        JSONObject order = paymentService.createOrder(request, email);
        return ResponseEntity.ok(order.toMap());
    }


    // ✅ VERIFY PAYMENT
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyPaymentRequest request, HttpServletRequest httpRequest)
            throws RazorpayException {
        String email = extractEmailFromToken(httpRequest);
        String msg = paymentService.verifyPayment(request, email);
        return ResponseEntity.ok(msg);
    }

    // ✅ Helper Method to safely extract JWT
    private String extractEmailFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomException("Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7).trim(); // ✅ removes 'Bearer ' and trims any space/newline
        if (!jwtProvider.validateToken(token)) {
            throw new CustomException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        return jwtProvider.getEmailFromToken(token);
    }
}
