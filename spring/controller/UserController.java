package com.example.spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.spring.config.Role;
import com.example.spring.model.Teacher;
import com.example.spring.model.Users;
import com.example.spring.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register Management (only Management users can do this)
    @PostMapping("/registerManagement")
    @PreAuthorize("hasRole('MANAGEMENT')")
    public ResponseEntity<String> registerManagement(@RequestBody Users userRequest) {
        if (userRequest.getRole() != Role.MANAGEMENT) {
            logger.warn("Attempt to register a non-management user.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Management can register Management users.");
        }
        userService.registerUser(userRequest);
        logger.info("Management user registered successfully: {}", userRequest.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body("Management user registered successfully.");
    }

    // Register Teacher (only Management users can do this)
    @PostMapping("/registerTeacher")
    @PreAuthorize("hasRole('MANAGEMENT')")
    public ResponseEntity<String> registerTeacher(@RequestBody Teacher teacherRequest) {
        try {
            userService.registerTeacher(teacherRequest);
            logger.info("Teacher registered successfully: {}", teacherRequest.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body("Teacher registered successfully.");
        } catch (RuntimeException e) {
            logger.error("Error registering teacher {}: {}", teacherRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // User login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Users user) {
        try {
            String token = userService.verify(user);
            logger.info("User {} logged in successfully.", user.getUsername());
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            logger.error("Login error for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @PostMapping("/login/teacher") // Endpoint for teacher login
public ResponseEntity<String> teacherLogin(@RequestBody Teacher teacher) {
    try {
        String token = userService.verifyTeacherLogin(teacher.getUsername(), teacher.getPassword());
        return ResponseEntity.ok(token);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}

}
