package com.example.springbootbackend.model.DTO;
import java.util.UUID;

import jakarta.persistence.Entity;


public class UserPersonalInfoDTO {
    private String username;
    private String role;
    private String address;
    private String email;
    private String userId;
    private String phone_number;
    private String name;

    public UserPersonalInfoDTO(String username, String role, String address, String email, UUID userId, String phone_number, String name) {
        this.username = username;
        this.role = role;
        this.address = address;
        this.email = email;
        this.userId = userId.toString();
        this.phone_number = phone_number;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public String getPhone_number() {
        return phone_number;
    }
    public String getName() {
        return name;
    }
}
