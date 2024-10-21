package com.example.spring.service;

import com.example.spring.model.Teacher;
import com.example.spring.repo.TeacherRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);
    private final TeacherRepo teacherRepo;
    private final PasswordEncoder passwordEncoder;

    public TeacherService(TeacherRepo teacherRepo, PasswordEncoder passwordEncoder) {
        this.teacherRepo = teacherRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('MANAGEMENT')")
    public Teacher register(Teacher teacher) {
        logger.debug("Attempting to register teacher: {}", teacher.getUsername());
        if (teacherRepo.existsByUsername(teacher.getUsername())) {
            logger.error("Duplicate teacher username: {}", teacher.getUsername());
            throw new IllegalArgumentException("Duplicate teacher username: " + teacher.getUsername());
        }
        
        teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        logger.info("Teacher registered successfully: {}", teacher.getUsername());
        return teacherRepo.save(teacher);
    }

    @PreAuthorize("hasAnyRole('MANAGEMENT', 'TEACHER')")
    public Optional<Teacher> getTeacherById(int id) {
        logger.debug("Fetching teacher by ID: {}", id);
        Optional<Teacher> teacher = teacherRepo.findById(id);
        if (teacher.isPresent()) {
            logger.info("Teacher found: {}", teacher.get().getUsername());
        } else {
            logger.warn("Teacher not found with ID: {}", id);
        }
        return teacher;
    }

    @PreAuthorize("hasRole('MANAGEMENT')")
    public List<Teacher> getAllTeachers() {
        logger.debug("Fetching all teachers");
        return teacherRepo.findAll();
    }
    
    public Teacher findTeacherByUsername(String username) {
        Optional<Teacher> teacherOptional = teacherRepo.findByUsername(username);
        return teacherOptional.orElse(null);  // Return null if teacher not found
    }

    @PreAuthorize("hasAnyRole('MANAGEMENT', 'TEACHER')")
    public Teacher updateTeacher(int id, Teacher teacher) {
        logger.debug("Attempting to update teacher with ID: {}", id);
        Optional<Teacher> existingTeacher = teacherRepo.findById(id);
        if (!existingTeacher.isPresent()) {
            logger.error("Teacher not found for update with ID: {}", id);
            throw new IllegalArgumentException("Teacher not found.");
        }
        if (teacherRepo.existsByUsername(teacher.getUsername()) && 
            !existingTeacher.get().getUsername().equals(teacher.getUsername())) {
            logger.error("Duplicate teacher username during update: {}", teacher.getUsername());
            throw new IllegalArgumentException("Duplicate teacher username.");
        }

        if (teacher.getPassword() != null) {
            logger.debug("Encoding password for teacher: {}", teacher.getUsername());
            teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        }

        teacher.setId(id);
        logger.info("Teacher updated successfully: {}", teacher.getUsername());
        return teacherRepo.save(teacher);
    }

    @PreAuthorize("hasRole('MANAGEMENT')")
    public void deleteTeacher(int id) {
        logger.debug("Attempting to delete teacher with ID: {}", id);
        if (!teacherRepo.existsById(id)) {
            logger.error("Teacher not found for deletion with ID: {}", id);
            throw new IllegalArgumentException("Teacher not found.");
        }
        teacherRepo.deleteById(id);
        logger.info("Teacher with ID {} deleted successfully.", id);
    }
}
