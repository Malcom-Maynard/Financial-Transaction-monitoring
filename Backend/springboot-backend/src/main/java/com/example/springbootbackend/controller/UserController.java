package com.example.springbootbackend.controller;

import com.example.springbootbackend.model.User;
import com.example.springbootbackend.model.DTO.UserDTO;
import com.example.springbootbackend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.springbootbackend.Service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService; 
    
    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // -------------------- CREATE USER --------------------
    @Operation(summary = "Add a new user", description = "Creates a new user based on data found in the request body.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    name = "UserCreated",
                    summary = "Successful user creation",
                    value = "{\n" +
                            "  \"userId\": \"a23f9c0d-1123-4a78-9f43-8a09f3aa1e9d\",\n" +
                            "  \"firstName\": \"John\",\n" +
                            "  \"lastName\": \"Doe\",\n" +
                            "  \"email\": \"john.doe@example.com\"\n" +
                            "}"
                ))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(
                    name = "InvalidUserInput",
                    summary = "Bad request example",
                    value = "{ \"message\": \"Invalid user data provided. Please check your request payload.\" }"
                )))
    })
    @PostMapping("/add")
    public ResponseEntity<Object> addUser(@RequestBody User user) {
        logger.info("API route Called: '/add'");
        AbstractMap<String,Object> validationData = userService.createUser(user);

        if (!validationData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationData);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

   
    @Operation(summary = "Get a user by ID", description = "Fetches a user record from the Backend using the provided UUID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "User found",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    name = "UserFound",
                    summary = "User retrieved successfully",
                    value = "{\n" +
                            "  \"userId\": \"a23f9c0d-1123-4a78-9f43-8a09f3aa1e9d\",\n" +
                            "  \"firstName\": \"Jane\",\n" +
                            "  \"lastName\": \"Smith\",\n" +
                            "  \"email\": \"jane.smith@example.com\"\n" +
                            "}"
                ))),
        @ApiResponse(responseCode = "400", description = "Invalid UUID format",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(
                    name = "InvalidUUID",
                    summary = "Bad UUID format",
                    value = "{ \"message\": \"Invalid UUID format: 1234\" }"
                ))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(
                    name = "UserNotFound",
                    summary = "User does not exist",
                    value = "{ \"message\": \"User with ID a23f9c0d-1123-4a78-9f43-8a09f3aa1e9d not found.\" }"
                )))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> GetUser(@PathVariable("id") String userID) {
        logger.info("API route Called: '/get' userID: " + userID);
        try {
            UUID uuid = UUID.fromString(userID);
            AbstractMap<String,Object> validationData = userService.GetUser(uuid);

            if (!validationData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationData);
            }

            User userFromDB = userRepository.findUsersByUserID(uuid);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(userFromDB);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid UUID format: " + userID);
        }
    }

   
    @Operation(summary = "Update an existing user", description = "Updates user details in the system using the provided UUID and request body.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "User updated successfully",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(
                    name = "UserUpdated",
                    summary = "Successful update",
                    value = "{ \"message\": \"# of users updated: 1\" }"
                ))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or UUID format",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(
                    name = "BadRequest",
                    summary = "Invalid request payload",
                    value = "{ \"message\": \"Invalid user data or UUID format.\" }"
                )))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Object> UpdateUser(@PathVariable("id") String userID, @RequestBody UserDTO updatedUserInfo)
            throws JsonProcessingException {

        logger.info("API route Called: '/ID'(PUT) userID: " + userID);
        try {
            UUID uuid = UUID.fromString(userID);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(updatedUserInfo);

            AbstractMap<String,Object> validationData = userService.UpdateUser(uuid, updatedUserInfo);

            if (!validationData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationData);
            }

            Integer usersUpdated = userRepository.UpdateUserInfo(json, uuid);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("# of users updated: " + usersUpdated);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid UUID format: " + userID);
        }
    }

    
    @Operation(summary = "Delete a user", description = "Deletes a user from the system using their UUID and user details in the request body.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "User deleted successfully",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(
                    name = "UserDeleted",
                    summary = "Successful deletion",
                    value = "{ \"message\": \"User has been deleted\" }"
                ))),
        @ApiResponse(responseCode = "400", description = "Invalid UUID or request data",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(
                    name = "InvalidDeleteRequest",
                    summary = "Bad request",
                    value = "{ \"message\": \"Invalid UUID format or request body.\" }"
                )))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> DeleteUser(@PathVariable("id") String userID, @RequestBody UserDTO currentUserID) {
        logger.info("API route Called: '/ID'(Delete) userID: " + userID);
        try {
            UUID uuid = UUID.fromString(userID);
            AbstractMap<String,Object> validationData = userService.DeleteUser(uuid, currentUserID.getUserId());

            if (!validationData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationData);
            }

            return ResponseEntity.status(HttpStatus.ACCEPTED).body("User has been deleted");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid UUID format: " + userID);
        }
    }
}
