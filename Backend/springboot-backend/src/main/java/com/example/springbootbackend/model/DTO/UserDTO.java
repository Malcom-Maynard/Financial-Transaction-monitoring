package com.example.springbootbackend.model.DTO;

import com.example.springbootbackend.Service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Created a Data Transfer Object for User to limit the exposure of sensitive data such as passwords when sending user data in API responses
@Entity
@Hidden
public class UserDTO  {

    @Id   
    @GeneratedValue(generator = "UUID")
    @Column(name = "user_id", updatable = false, nullable = false)
    @JsonProperty("id")  // Rename "userId" to "id" in the JSON output
    @Schema(description = "User ID")
    private UUID userId;

    @Schema(description = "Users full name")
    private String name;

    @Schema(description = "Users role")
    private String role;

    @Schema(description = "Users phone number")  
    private String phoneNumber;

    @Schema(description = "Users email address")
    private String email;
    
    @Schema(description = "Users username")
    private String username;

    @Schema(description = "Users address")
    private String address;
    
    @JsonIgnore
    private String password;
    

    //Print user data in a readable format, used early in development for debugging
    public String printData (){

        String msg = "\n\nData for user ID: "+this.getUserId()+
        "\n"+"name: "+this.getName() +
        "\n"+"role: "+this.getRole() +
        "\n"+"phoneNumber: "+this.getPhoneNumber() +
        "\n"+"email: "+this.getEmail() +
        "\n"+"Address: "+this.getAddress()+
        "\n"+"username: "+this.getUsername() +
        "\n"+"password: "+this.getPassword()+"\n\n" ;

        return msg;
    }

     //Print user data in a readable format, used early in development for debugging within a JSON format
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



    public String getAddress() {
        return address;
    }

    public void setAddress(String Address) {
        this.address= Address;
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
