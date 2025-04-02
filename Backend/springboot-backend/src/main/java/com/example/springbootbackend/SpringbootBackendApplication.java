package com.example.springbootbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = "com.example.springbootbackend.model") // Ensure correct package
@EnableJpaRepositories(basePackages = "com.example.springbootbackend.repository") // Ensure correct package
@SpringBootApplication
public class SpringbootBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootBackendApplication.class, args);
    }
}
