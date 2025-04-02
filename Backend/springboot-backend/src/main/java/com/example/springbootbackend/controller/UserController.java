package com.example.springbootbackend.controller;

import com.example.springbootbackend.model.User;
import com.example.springbootbackend.repository.UserRepository;
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
    

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @RequestMapping("/")
    public String home() {
        return "Welcome to the homepage!";
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addUser(@RequestBody User user) {

        logger.info("API route Called: '/add'");
        
        
        //Send to the Service layer for processings of new user information

        

        AbstractMap<String,String> VaildationData = userService.createUser(user);
        
        logger.info("VaildationData sIZE "+VaildationData.size());

        if(VaildationData.size()!=0){
            logger.info("TRIGGERED TRIGGERED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VaildationData);

        }



        return ResponseEntity.status(HttpStatus.CREATED).body(user);
        
    }

    
}
