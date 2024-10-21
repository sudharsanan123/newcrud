package com.example.spring.service;
import com.example.spring.model.Student;
import com.example.spring.repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepo studentRepo;

    // Method for Management to add a student (STUDENT role cannot access)
    @PreAuthorize("hasRole('MANAGEMENT')")
    public Student addStudent(Student student) {
        if (studentRepo.existsByName(student.getName())) {
            throw new IllegalArgumentException("Duplicate student name: " + student.getName());
        }
        return studentRepo.save(student);
    }

    // Method to get all students - accessible to MANAGEMENT, TEACHER, and STUDENT
    @PreAuthorize("hasAnyRole('MANAGEMENT', 'TEACHER', 'STUDENT')")
    public List<Student> getStudents() {
        return studentRepo.findAll();
    }

    // Method to update a student - accessible to MANAGEMENT and TEACHER only
    @PreAuthorize("hasAnyRole('MANAGEMENT', 'TEACHER')")
    public Student updateStudent(int id, Student updatedStudent) {
        if (!studentRepo.existsById(id)) {
            throw new IllegalArgumentException("Student with id " + id + " not found.");
        }

        if (studentRepo.existsByName(updatedStudent.getName()) && studentRepo.findByName(updatedStudent.getName()).get().getId() != id) {
            throw new IllegalArgumentException("Duplicate student name: " + updatedStudent.getName());
        }

        updatedStudent.setId(id);
        return studentRepo.save(updatedStudent);
    }

    // Method to delete a student - accessible to MANAGEMENT only
    @PreAuthorize("hasRole('MANAGEMENT')")
    public boolean deleteStudent(int id) {
        if (!studentRepo.existsById(id)) {
            throw new IllegalArgumentException("Student with id " + id + " not found.");
        }
        studentRepo.deleteById(id);
        return true;
    }

    // Method to get a student by ID - accessible to MANAGEMENT, TEACHER, and the student themselves
    @PreAuthorize("hasRole('MANAGEMENT') or hasRole('TEACHER') or @studentSecurity.hasAccessToStudent(#id)")
    public Optional<Student> getStudentById(int id) {
        return studentRepo.findById(id);
    }
}
