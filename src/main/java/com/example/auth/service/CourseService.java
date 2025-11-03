package com.example.auth.service;

import com.example.auth.dto.CreateCourseRequest;
import com.example.auth.entity.Course;
import com.example.auth.entity.Role;
import com.example.auth.entity.User;
import com.example.auth.exception.CustomException;
import com.example.auth.repository.CourseRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;

    public Course createCourse(CreateCourseRequest request, MultipartFile video, MultipartFile file, String email) {
        User creator = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("Creator not found with email: " + email, HttpStatus.NOT_FOUND));

        if (creator.getRole() != Role.CREATOR) {
            throw new CustomException("Only creators can create courses.", HttpStatus.FORBIDDEN);
        }

        String videoUrl = null;
        String fileUrl = null;

        if (video != null && !video.isEmpty()) {
            videoUrl = cloudinaryService.uploadFile(video, "E_Learning/videos");
        }
        if (file != null && !file.isEmpty()) {
            fileUrl = cloudinaryService.uploadFile(file, "E_Learning/files");
        }

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .videoUrl(videoUrl)
                .fileUrl(fileUrl)
                .creator(creator)
                .build();

        return courseRepository.save(course);
    }

    public List<Course> getAllCourses(String userEmail) {
        List<Course> courses = courseRepository.findAll();

        if (userEmail == null) {
            return courses.stream()
                    .map(this::hideSensitiveContent)
                    .collect(Collectors.toList());
        }

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            return courses.stream()
                    .map(this::hideSensitiveContent)
                    .collect(Collectors.toList());
        }

        return courses.stream()
                .map(course -> {
                    if (course.getCreator().getId().equals(user.getId())) {
                        return course;
                    }
                    if (user.getRole() == Role.ADMIN) {
                        return course;
                    }
                    if (user.getEnrolledCourses() != null && user.getEnrolledCourses().contains(course)) {
                        return course;
                    }
                    return hideSensitiveContent(course);
                })
                .collect(Collectors.toList());
    }

    public Course getCourseById(Long id, String userEmail) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CustomException("Course not found with id: " + id, HttpStatus.NOT_FOUND));

        if (userEmail == null) {
            return hideSensitiveContent(course);
        }

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            return hideSensitiveContent(course);
        }

        if (course.getCreator().getId().equals(user.getId())) {
            return course;
        }

        if (user.getRole() == Role.ADMIN) {
            return course;
        }

        if (user.getEnrolledCourses() != null && user.getEnrolledCourses().contains(course)) {
            return course;
        }

        return hideSensitiveContent(course);
    }

    private Course hideSensitiveContent(Course course) {
        return Course.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .price(course.getPrice())
                .creator(course.getCreator())
                .videoUrl(null)
                .fileUrl(null)
                .build();
    }

    public Course updateCourse(Long id, CreateCourseRequest request, MultipartFile video, MultipartFile file, String email) {
        User creator = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("Creator not found with email: " + email, HttpStatus.NOT_FOUND));

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CustomException("Course not found", HttpStatus.NOT_FOUND));

        if (!course.getCreator().getId().equals(creator.getId())) {
            throw new CustomException("You can only update your own courses.", HttpStatus.FORBIDDEN);
        }

        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getCategory() != null) course.setCategory(request.getCategory());
        if (request.getPrice() != null) course.setPrice(request.getPrice());

        if (video != null && !video.isEmpty()) {
            course.setVideoUrl(cloudinaryService.uploadFile(video, "E_Learning/videos"));
        }
        if (file != null && !file.isEmpty()) {
            course.setFileUrl(cloudinaryService.uploadFile(file, "E_Learning/files"));
        }

        return courseRepository.save(course);
    }

    public void deleteCourse(Long id, String email) {
        User creator = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("Creator not found with email: " + email, HttpStatus.NOT_FOUND));

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CustomException("Course not found", HttpStatus.NOT_FOUND));

        if (!course.getCreator().getId().equals(creator.getId())) {
            throw new CustomException("You can only delete your own courses.", HttpStatus.FORBIDDEN);
        }

        courseRepository.delete(course);
    }

    public List<Course> getCoursesByCreator(String email) {
        User creator = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("Creator not found with email: " + email, HttpStatus.NOT_FOUND));
        return courseRepository.findByCreator(creator);
    }
}