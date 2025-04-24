package com.example.springbootbackend.controller;

import com.example.springbootbackend.model.User;
import com.example.springbootbackend.model.DTO.UserDTO;
import com.example.springbootbackend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.springbootbackend.rabbitmq.MessageSender;
import com.example.springbootbackend.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private UserService userService; 
    
    @Autowired
    private UserRepository userRepository;  // Injecting the Repository layer

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @RequestMapping("/")
    public String home() {
        return "Welcome to the homepage!";
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addUser(@RequestBody User user) {

        logger.info("API route Called: '/add'");
        
        
        //Send to the Service layer for processings of new user information

        

        AbstractMap<String,Object> VaildationData = userService.createUser(user);
        
        logger.info("VaildationData sIZE "+VaildationData.size());

        if(VaildationData.size()!=0){
            logger.info("TRIGGERED TRIGGERED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VaildationData);

        }



        return ResponseEntity.status(HttpStatus.CREATED).body(user);
        
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> GetUser(@PathVariable("id") String userID) {

        logger.info("API route Called: '/get' userID: "+userID);
        
        try {


            UUID uuid = UUID.fromString(userID);
            

            AbstractMap<String,Object> VaildationData = userService.GetUser(uuid);
        
    
            if(VaildationData.size()!=0){
                logger.info("Vaildation messages being  sent back");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VaildationData);
    
            }
    
    
            User UsersFromDB = userRepository.findUsersByUserID(uuid);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(UsersFromDB);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid UUID format: " + userID);
        }
        

        

      
        
            
        
        
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> UpdateUser(@PathVariable("id") String userID, 
    @RequestBody UserDTO UpdatedUserInfo
    ) throws JsonProcessingException {

        logger.info("API route Called: '/ID'(PUT) userID: "+userID);
        
        try {


            UUID uuid = UUID.fromString(userID);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(UpdatedUserInfo);

            logger.info("Object to string "+UpdatedUserInfo.JSONOutput());


            AbstractMap<String,Object> VaildationData = userService.UpdateUser(uuid,UpdatedUserInfo);
            logger.info( "Size of the vaildation map: "+Integer.toString(VaildationData.size()));
        
            if(VaildationData.size()!=0){
                logger.info("Vaildation messages being sent back");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VaildationData);
    
            }
            
            Integer UsersFromDB = userRepository.UpdateUserInfo(json,uuid);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("# of users updated: "+UsersFromDB);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid UUID format: " + userID);
        }
        
        
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> DeleteUser(@PathVariable("id") String userID,
    @RequestBody UserDTO CurrentUserID
    ) {

        logger.info("API route Called: '/ID'(Delete) userID: "+userID);
        
        try {


            UUID uuid = UUID.fromString(userID);
            


            AbstractMap<String,Object> VaildationData = userService.DeleteUser(uuid,CurrentUserID.getUserId());
            logger.info( "Size of the vaildation map: "+Integer.toString(VaildationData.size()));
        
            if(VaildationData.size()!=0){
                logger.info("Vaildation messages being sent back");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VaildationData);
    
            }
            
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid UUID format: " + userID);
        }
        
    }


   
    

    
}
