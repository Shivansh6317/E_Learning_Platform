
package com.example.auth.controller;

import com.example.auth.entity.Course;
import com.example.auth.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STUDENT','CREATOR','ADMIN')")
public class StudentController {

    private final StudentService studentService;


    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getEnrolledCourses(Principal principal) {
        return ResponseEntity.ok(studentService.getEnrolledCourses(principal.getName()));
    }
}
