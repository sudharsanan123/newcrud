package com.example.spring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.spring.config.Role;
import com.example.spring.model.Teacher;
import com.example.spring.model.UserPrincipal;
import com.example.spring.model.Users;
import com.example.spring.repo.TeacherRepo;
import com.example.spring.repo.UserRepo;
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepo repo;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authManager;
    
    @Autowired
    private TeacherRepo teacherRepository;

    @Autowired
    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.repo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerTeacher(Teacher teacher) {
        if (teacherRepository.existsByUsername(teacher.getUsername())) {
            throw new RuntimeException("Teacher with this username already exists.");
        }

        // Encrypt the password before saving
        teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        teacherRepository.save(teacher);
        logger.info("Teacher {} registered successfully.", teacher.getUsername());
    }

    public void registerUser(Users userRequest) {
        String normalizedUsername = userRequest.getUsername().toLowerCase();

        if (userExists(normalizedUsername)) {
            logger.warn("Registration failed: User with username {} already exists.", normalizedUsername);
            throw new RuntimeException("User already exists");
        }

        try {
            Users user = new Users();
            user.setUsername(normalizedUsername);
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            user.setRole(userRequest.getRole());
            repo.save(user);
            logger.info("User {} registered successfully.", normalizedUsername);
        } catch (Exception e) {
            logger.error("Error while registering user {}: {}", normalizedUsername, e.getMessage());
        }
    }

    public String verify(Users user) {
        String normalizedUsername = user.getUsername().toLowerCase();
        logger.info("UserService: Authenticating user: {}", normalizedUsername);

        try {
            Users existingUser = repo.findByUsername(normalizedUsername)
                    .orElseThrow(() -> {
                        logger.warn("UserService: User not found: {}", normalizedUsername);
                        return new RuntimeException("User not found");
                    });

            if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                logger.warn("UserService: Invalid credentials for user: {}", normalizedUsername);
                throw new RuntimeException("Invalid username or password");
            }

            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(existingUser.getUsername(), user.getPassword()));

            if (authentication.isAuthenticated()) {
                UserPrincipal authenticatedUser = (UserPrincipal) authentication.getPrincipal();
                Role userRole = authenticatedUser.getRole();

                String token = jwtService.generateToken(authenticatedUser.getUsername(), userRole);
                logger.info("UserService: Token generated for user: {}", normalizedUsername);
                return token;
            }

        } catch (Exception e) {
            logger.error("UserService: Authentication failed for user: {}", normalizedUsername, e);
            throw new RuntimeException("Authentication failed. Please check your credentials.");
        }

        logger.warn("UserService: Authentication failed for user: {}", normalizedUsername);
        return "fail";
    }

    public boolean userExists(String username) {
        boolean exists = repo.findByUsername(username).isPresent();
        logger.debug("UserService: Checking existence of user {}: {}", username, exists);
        return exists;
    }

    public String verifyTeacherLogin(String username, String password) {
        Teacher teacher = teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found."));

        if (!passwordEncoder.matches(password, teacher.getPassword())) {
            throw new RuntimeException("Invalid password.");
        }

        return jwtService.generateToken(teacher.getUsername(), Role.TEACHER);
    }
}

