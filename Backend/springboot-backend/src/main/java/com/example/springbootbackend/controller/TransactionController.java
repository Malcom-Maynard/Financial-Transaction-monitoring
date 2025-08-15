package com.example.springbootbackend.controller;


import com.example.springbootbackend.model.Transaction;
import com.example.springbootbackend.model.User;
import com.example.springbootbackend.repository.UserRepository;
import com.example.springbootbackend.Service.*;
import com.example.springbootbackend.Service.rabbitmq.MessageConsumer;
import com.example.springbootbackend.Service.rabbitmq.MessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.AbstractMap;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/Transaction")
//@PreAuthorize("isAuthenticated()") 
public class TransactionController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
 
    

    @Autowired
    private MessageConsumer messageConsumer;

    @Autowired
    private TransactionService transactionService;

    
    @PostMapping("/add")
    public ResponseEntity<Object> addTransaction(@RequestBody Transaction transaction ) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(transaction);
        

        logger.info("API route Called(Transaction): '/add'");
        logger.info("(Transaction): '/add - Infomation from request: "+json);
        
        
        AbstractMap<String,Object> VaildationData = transactionService.createTransaction(transaction);

        logger.info("VaildationData sIZE "+VaildationData.size());

        if(VaildationData.size()!=0){
            logger.info("ERROR: Issue with Transaction Data being sent from API call ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VaildationData);

        }



        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction infomation Created");

       



        
    }












}
