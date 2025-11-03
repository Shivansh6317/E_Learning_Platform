package com.example.auth.service;

import com.example.auth.dto.CreateOrderRequest;
import com.example.auth.dto.VerifyPaymentRequest;
import com.example.auth.entity.Course;
import com.example.auth.entity.Payment;
import com.example.auth.entity.User;
import com.example.auth.exception.CustomException;
import com.example.auth.repository.CourseRepository;
import com.example.auth.repository.PaymentRepository;
import com.example.auth.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    // injected from properties
    @Value("${RZPAY_Id}")
    private String razorpayKeyId;

    @Value("${RZPAY_SECRET}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    public PaymentService(PaymentRepository paymentRepository,
                          UserRepository userRepository,
                          CourseRepository courseRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @PostConstruct
    public void init() throws Exception {
        this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
    }

    public JSONObject createOrder(CreateOrderRequest request, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new CustomException("Course not found", HttpStatus.NOT_FOUND));
            if (user.getEnrolledCourses() != null && user.getEnrolledCourses().contains(course)) {
                throw new CustomException("You are already enrolled in this course", HttpStatus.BAD_REQUEST);
            }

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (course.getPrice() * 100)); // paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

            Order order = razorpayClient.orders.create(orderRequest);

            Payment payment = Payment.builder()
                    .razorpayOrderId(order.get("id"))
                    .amount(course.getPrice())
                    .status("PENDING")
                    .user(user)
                    .course(course)
                    .build();

            paymentRepository.save(payment);

            return order.toJson();
        } catch (RazorpayException e) {
            throw new CustomException("Failed to create Razorpay order: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new CustomException("Unexpected error while creating order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String verifyPayment(VerifyPaymentRequest request, String userEmail) {
        try {
            Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                    .orElseThrow(() -> new CustomException("Order not found", HttpStatus.NOT_FOUND));

            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", request.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
            attributes.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);

            if (!isValid) {
                payment.setStatus("FAILED");
                paymentRepository.save(payment);
                throw new CustomException("Invalid payment signature", HttpStatus.BAD_REQUEST);
            }

            payment.setStatus("SUCCESS");
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            paymentRepository.save(payment);

            User user = payment.getUser();
            Course course = payment.getCourse();
            if (user != null && course != null) {
                if (user.getEnrolledCourses() == null) {
                    user.setEnrolledCourses(new HashSet<>());
                }
                user.getEnrolledCourses().add(course);
                userRepository.save(user);
            }

            return "Payment verified successfully and student enrolled.";

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error verifying payment: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}