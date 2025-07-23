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
import com.example.springbootbackend.Service.rabbitmq.MessageSender;
import com.example.springbootbackend.model.User;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepo;  

    @Autowired
    private VaildationService ValdSer;

    @Autowired
    private MessageSender messageSender;

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    ObjectMapper mapper = new ObjectMapper();
    

    // Method to create a new user
    public AbstractMap<String,Object> createTransaction(Transaction transaction) throws JsonProcessingException {
        String json = mapper.writeValueAsString(transaction);
        logger.info("createTransaction - Data from Transaction: "+json);


        //Step 1: Vaildate incoming data from the API Request
        AbstractMap<String,Object> VaildationMessages = ValdSer.validateTransactionInfomation(transaction);
           
        // Step 2: Send the data through RabbitMQ for processing
          if(VaildationMessages.isEmpty()){

            //Send the transaction to be checked for if it is fraudlect or not 
            messageSender.sendMessageIN(transaction);


        }

        /* Step 3: Run a query to check if the data was processed and not flagged

            i) Use repo call and use the UserID, sort by the lastest date and check that the date is the same as the System date

            a) If no errors with this
                - Set the status of transaction to be completed
            
            b) If there an issue 
                - Set the status as bad and tell the user that their transaction failed
        */


        //Change User address to follow this data struct Street Name, City, Province, Area code, Country


        /*
            Step 4: Check if the transaction is bad or not ( Use the service )
         *  - Service will preform a set of checks on the transaction Data and will either give it one of the following status 
         *      O Completed
         *      O Failed
         *  -  it will log it into the Database with the transaction table either it was complted or not 
        */


        //Step 3A: if transaction is good, send the transaction ID and the status back to the user

        //Step 3B: If failed transaction: Log the infomation inside of the alerts Database and send the message back to the through RabbitMQ
    


        
    
     
        
        return VaildationMessages;
        
        
    }



    
}
