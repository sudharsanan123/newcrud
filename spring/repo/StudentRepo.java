package com.example.spring.repo;

import com.example.spring.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepo extends JpaRepository<Student, Integer> {
    Optional<Student> findByName(String name); // Correct the method name
    boolean existsByName(String name); // Method to check if a name already exists
}
