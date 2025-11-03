package com.example.auth.service;

import com.example.auth.entity.Course;
import com.example.auth.entity.User;
import com.example.auth.exception.CustomException;
import com.example.auth.repository.CourseRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;


    public void enrollInCourse(Long courseId, String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found with email: " + email, HttpStatus.NOT_FOUND));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException("Course not found with ID: " + courseId, HttpStatus.NOT_FOUND));
        if (student.getEnrolledCourses() == null) {
            student.setEnrolledCourses(new HashSet<>());
        }
        if (student.getEnrolledCourses().contains(course)) {
            throw new CustomException("Already enrolled in this course.", HttpStatus.BAD_REQUEST);
        }

        student.getEnrolledCourses().add(course);
        userRepository.save(student);
    }


    public List<Course> getEnrolledCourses(String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found with email: " + email, HttpStatus.NOT_FOUND));
        return new ArrayList<>(student.getEnrolledCourses());
    }
}

