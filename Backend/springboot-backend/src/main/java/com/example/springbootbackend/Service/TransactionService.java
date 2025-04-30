package com.example.springbootbackend.Service;


import com.example.springbootbackend.model.Transaction;
import com.example.springbootbackend.model.User;
import com.example.springbootbackend.model.DTO.UserDTO;
import com.example.springbootbackend.repository.TransactionRepository;
import com.example.springbootbackend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.AbstractMap;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.springbootbackend.Service.VaildationService;
import com.example.springbootbackend.model.User;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepo;  

    @Autowired
    private VaildationService ValdSer;

        
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    ObjectMapper mapper = new ObjectMapper();
    

    // Method to create a new user
    public AbstractMap<String,Object> createTransaction(Transaction transaction) throws JsonProcessingException {
        // Perform business logic, such as validation or other processing if needed

        String json = mapper.writeValueAsString(transaction);

        logger.info("Data from Transaction: "+json);

    
        AbstractMap<String,Object> VaildationMessages = ValdSer.validateTransactionInfomation(transaction);
        // Save user to the database via the repository
    
     
        
        return VaildationMessages;
        
        
    }



    
}
