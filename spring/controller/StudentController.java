package com.example.spring.controller;

import com.example.spring.model.Student;
import com.example.spring.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // Allow ALL to view all students
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGEMENT', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<Student>> getStudents() {
        return ResponseEntity.ok(studentService.getStudents());
    }

    // Allow TEACHER and MANAGEMENT to view a student by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGEMENT', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Allow MANAGEMENT only to add a new student
    @PostMapping
    @PreAuthorize("hasRole('MANAGEMENT')")
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        Student savedStudent = studentService.addStudent(student);
        return ResponseEntity.ok(savedStudent);
    }

    // Allow MANAGEMENT and TEACHER to update a student
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGEMENT', 'TEACHER')")
    public ResponseEntity<Student> updateStudent(@PathVariable int id, @RequestBody Student student) {
        Student updatedStudent = studentService.updateStudent(id, student);
        return ResponseEntity.ok(updatedStudent);
    }

    // Allow MANAGEMENT only to delete a student
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGEMENT')")
    public ResponseEntity<Void> deleteStudent(@PathVariable int id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
