package com.example.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.spring.model.Teacher;
import com.example.spring.model.UserPrincipal;
import com.example.spring.model.Users;
import com.example.spring.repo.UserRepo;
import com.example.spring.repo.TeacherRepo; // Import your TeacherRepo

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TeacherRepo teacherRepo; // Inject TeacherRepo

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First check in Users repository
        Optional<Users> userOptional = userRepo.findByUsername(username);
        if (userOptional.isPresent()) {
            return new UserPrincipal(userOptional.get());
        }

        // Then check in Teacher repository (assuming you have a similar method)
        Optional<Teacher> teacherOptional = teacherRepo.findByUsername(username); // Adjust this based on your Teacher model
        if (teacherOptional.isPresent()) {
            return new UserPrincipal(teacherOptional.get()); // You may need to adjust UserPrincipal to handle teachers
        }

        // If neither found, throw exception
        throw new UsernameNotFoundException("User not found: " + username);
    }
}
