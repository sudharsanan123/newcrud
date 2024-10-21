package com.example.spring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity // Mark this class as an entity
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID starting from 1
    private int id;
    private String name;
    private String email;
    private int age;

    // No-argument constructor
    public Student() {
    }

    // Constructor that matches your test
    public Student(int id, String name, String email) {
        this(id, name, email, 0); // Calls the full constructor with age set to 0
    }

    // Constructor with all parameters
    public Student(int id, String name, String email, int age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    // Constructor with name, email, and age (id defaults to 0)
    public Student(String name, String email, int age) {
        this(1, name, email, age); // Calls the full constructor with id set to 1
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student student = (Student) obj;
        return age == student.age && 
               id == student.id &&
               Objects.equals(name, student.name) && 
               Objects.equals(email, student.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, age);
    }
}
