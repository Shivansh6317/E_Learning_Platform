package com.example.auth.controller;

import com.example.auth.dto.CreateCourseRequest;
import com.example.auth.entity.Course;
import com.example.auth.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PreAuthorize("hasRole('CREATOR')")
    @PostMapping("/create")
    public ResponseEntity<Course> createCourse(
            @ModelAttribute CreateCourseRequest request,
            @RequestPart(required = false) MultipartFile video,
            @RequestPart(required = false) MultipartFile file,
            Principal principal) {

        Course createdCourse = courseService.createCourse(request, video, file, principal.getName());
        return ResponseEntity.ok(createdCourse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Course>> getAllCourses(Principal principal) {
        // Pass principal (can be null if not authenticated)
        String email = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(courseService.getAllCourses(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id, Principal principal) {
        // Pass principal (can be null if not authenticated)
        String email = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(courseService.getCourseById(id, email));
    }

    @PreAuthorize("hasRole('CREATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @ModelAttribute CreateCourseRequest request,
            @RequestPart(required = false) MultipartFile video,
            @RequestPart(required = false) MultipartFile file,
            Principal principal) {

        Course updatedCourse = courseService.updateCourse(id, request, video, file, principal.getName());
        return ResponseEntity.ok(updatedCourse);
    }

    @PreAuthorize("hasRole('CREATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id, Principal principal) {
        courseService.deleteCourse(id, principal.getName());
        return ResponseEntity.ok("Course deleted successfully.");
    }

    @PreAuthorize("hasRole('CREATOR')")
    @GetMapping("/my-courses")
    public ResponseEntity<List<Course>> getMyCourses(Principal principal) {
        return ResponseEntity.ok(courseService.getCoursesByCreator(principal.getName()));
    }
}