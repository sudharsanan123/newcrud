package com.example.spring; // Ensure this package matches your project structure
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.spring.model")
public class ApplicationMain {
    public static void main(String[] args) {

        SpringApplication.run(ApplicationMain.class, args);

    }

}

//Admin management->teacher->student-Hierarchy
//Quartz Job ->for every 10 second -> job should run

// git config --global user.name "sudharsanan123"
// git config --global user.email "sanansudhar7@gmail.com"
