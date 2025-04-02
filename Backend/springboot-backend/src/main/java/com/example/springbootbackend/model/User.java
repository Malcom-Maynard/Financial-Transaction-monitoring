package com.example.springbootbackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

import java.util.UUID;
import java.util.logging.Logger;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;
import org.slf4j.LoggerFactory;

import com.example.springbootbackend.Service.UserService;

@Entity
public class User {

    @Id   
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    private String name;

    private String role;

    private String phoneNumber;

    private String email;
    
    
    private String username;

    
    private String password;

    
    
    public String printData (){

        String msg = "\n\nData for user ID: "+this.getUserId()+
        "\n"+"name: "+this.getName() +
        "\n"+"role: "+this.getRole() +
        "\n"+"phoneNumber: "+this.getPhoneNumber() +
        "\n"+"email: "+this.getEmail() +
        "\n"+"username: "+this.getUsername() +
        "\n"+"password: "+this.getPassword()+"\n\n" ;




        return msg;

        
        
       



    }
    // Getters and setters

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
