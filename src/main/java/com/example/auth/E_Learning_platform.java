package com.example.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class E_Learning_platform {

    public static void main(String[] args) {
        SpringApplication.run(E_Learning_platform.class, args);
    }
    @Bean
    CommandLineRunner seedCourses(com.example.auth.repository.CourseRepository courseRepository) {
        return args -> {
            if (courseRepository.count() == 0) {
                courseRepository.save(new com.example.auth.entity.Course(null, "Java Masterclass", "Complete Java Course", 499));
                courseRepository.save(new com.example.auth.entity.Course(null, "Spring Boot Deep Dive", "Advanced Spring Boot", 599));
            }
        };
    }

}
