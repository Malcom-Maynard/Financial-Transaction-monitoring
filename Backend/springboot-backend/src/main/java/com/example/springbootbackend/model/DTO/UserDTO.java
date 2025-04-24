package com.example.springbootbackend.model.DTO;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.springbootbackend.Service.UserService;
import com.example.springbootbackend.model.User;

public class UserDTO   {
    private UUID userId;
    private String name;
    private String role;
    private String phoneNumber;
    private String email;
    private String username;


    

    // Getters and Setters
     private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    public String printData (){

        String msg = "\n\nData for user ID: "+this.getUserId()+
        "\n"+"name: "+this.getName() +
        "\n"+"role: "+this.getRole() +
        "\n"+"phoneNumber: "+this.getPhoneNumber() +
        "\n"+"email: "+this.getEmail() +
        "\n"+"username: "+this.getUsername() +"\n\n";
        




        return msg;
    }

    
    public String JSONOutput() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
    
        boolean hasPrevious = false;
    
        if (getName() != null) {
            jsonBuilder.append("\"name\": \"").append(getName()).append("\"");
            hasPrevious = true;
        }
    
        if (getEmail() != null) {
            if (hasPrevious) jsonBuilder.append(", ");
            jsonBuilder.append("\"email\": \"").append(getEmail()).append("\"");
            hasPrevious = true;
        }
    
        if (getPhoneNumber() != null) {
            if (hasPrevious) jsonBuilder.append(", ");
            jsonBuilder.append("\"phone_number\": \"").append(getPhoneNumber()).append("\"");
            hasPrevious = true;
        }
    
        if (getUsername() != null) {
            if (hasPrevious) jsonBuilder.append(", ");
            jsonBuilder.append("\"username\": \"").append(getUsername()).append("\"");
            hasPrevious = true;
        }
    
        if (getUserId() != null) {
            if (hasPrevious) jsonBuilder.append(", ");
            jsonBuilder.append("\"user_id\": \"").append(getUserId()).append("\"");
        }
    
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
    
    // Getters and Setters

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    
}
