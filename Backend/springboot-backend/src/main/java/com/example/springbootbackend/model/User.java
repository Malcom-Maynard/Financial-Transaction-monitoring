package com.example.springbootbackend.model;

import com.example.springbootbackend.Service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Schema(description = "User Entity object representing a user in the system")
public class User {

    @Id   
    @GeneratedValue(generator = "UUID")
    @Column(name = "user_id", updatable = false, nullable = false)
    @JsonProperty("id")  // Rename "userId" to "id" in the JSON output
    @Schema(description = "Unique identifier for the user", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "Full name of the user", example = "John Doe")
    private String name;

    @Schema(description = "Role of the user in the system", example = "admin")
    private String role;

    @Schema(description = "Phone number of the user", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "Email address of the user", example = "JohnDoe1999@gmail.com")
    private String email;
    
    @Schema(description = "Username for login", example = "johndoe")    
    private String username;

    @Schema(description = "Address of the user", example = "John's Home, 123 Main St, City, Country")
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
