package com.example.springbootbackend.controller;

import com.example.springbootbackend.model.Transaction;
import com.example.springbootbackend.model.User;
import com.example.springbootbackend.model.DTO.TransactionDTO;
import com.example.springbootbackend.repository.UserRepository;
import com.example.springbootbackend.Service.TransactionService;
import com.example.springbootbackend.Service.UserService;
import com.example.springbootbackend.Service.rabbitmq.MessageConsumer;
import com.example.springbootbackend.Service.rabbitmq.MessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractMap;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/Transaction")
//@PreAuthorize("isAuthenticated()")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/add")
    public ResponseEntity<Object> addTransaction(@RequestBody Transaction transaction) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(transaction);

        logger.info("API route Called(Transaction): '/add'");
        logger.info("(Transaction): '/add - Information from request: " + json);

        AbstractMap<String, Object> validationData = transactionService.createTransaction(transaction);

        logger.info("ValidationData SIZE(Transaction): " + validationData.size());

        if (validationData.size() != 0 && !validationData.containsKey("Transaction Status")) {
            logger.info("ERROR: Issue with Transaction Data being sent from API call ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationData);
        }

        if (validationData.containsKey("Transaction Status")) {
            logger.info("Transaction information Created successfully with status: " + validationData.get("Transaction Status"));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Transaction information Created: " + validationData.get("Transaction Status"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction information Created: " + validationData);
    }

    // GET transaction API call   

    @GetMapping("/{transactionID}")
    public ResponseEntity<Object> getTransactionById(@PathVariable String transactionID, @RequestBody Transaction transaction) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(transaction);

        logger.info("API route Called(Transaction): '/{id}'");
        logger.info("(Transaction): '/{id} - Information from request(Header): " + transactionID);
        logger.info("(Transaction): '/{id} - Information from request(Body): " + json);

        UUID uuid = UUID.fromString(transactionID);

        AbstractMap<String, Object> VaildationData = transactionService.getTransaction(uuid, transaction);

        logger.info("VaildationData SIZE(Transaction get): " + VaildationData);

        if (VaildationData.size() != 0 && !VaildationData.containsKey("Transaction Data")) {
            logger.info("ERROR: Issue with Transaction Data being sent from API call ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VaildationData);
        }

        if (VaildationData.containsKey("Transaction Data")) {
            TransactionDTO transactionDTO = new TransactionDTO((Transaction) VaildationData.get("Transaction Data"));
            return ResponseEntity.status(HttpStatus.OK).body(transactionDTO);
        }

        // Always send the validation message as well
        return ResponseEntity.status(HttpStatus.OK).body(VaildationData);
    }

    // Update transaction API call
    @PutMapping("/update/{transactionID}")
    public ResponseEntity<Object> updateTransaction(@PathVariable String transactionID, @RequestBody Transaction transaction) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(transaction);

        logger.info("API route Called(Transaction): '/update/{id}'");
        logger.info("(Transaction): '/update/{id} - Information from request(Header): " + transactionID);
        logger.info("(Transaction): '/update/{id} - Information from request(Body): " + json);

        UUID uuid = UUID.fromString(transactionID);

        AbstractMap<String, Object> validationData = transactionService.updateTransaction(uuid, transaction);

        logger.info("ValidationData SIZE(Transaction update): " + validationData.size());

        if (validationData.size() != 0 && !validationData.containsKey("Transaction Status")) {
            logger.info("ERROR: Issue with Transaction Data being sent from API call ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationData);
        }

        if (validationData.containsKey("Transaction Status")) {
            logger.info("Transaction information Updated successfully with status: " + validationData.get("Transaction Status"));
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Transaction information Updated: " + validationData.get("Transaction Status"));
        }

        return ResponseEntity.status(HttpStatus.OK).body("Transaction information Updated: " + validationData);
    }

    // Delete transaction API call
    @DeleteMapping("/delete/{transactionID}")
    public ResponseEntity<Object> deleteTransaction(@PathVariable String transactionID, @RequestBody Transaction transaction) throws JsonProcessingException {
        logger.info("API route Called(Transaction): '/delete/{id}'");
        logger.info("(Transaction): '/delete/{id} - Information from request(Header): " + transactionID);

        UUID uuid = UUID.fromString(transactionID);

        AbstractMap<String, Object> validationData = transactionService.deleteTransaction(uuid, transaction);

        logger.info("ValidationData SIZE(Transaction delete): " + validationData.size());

        if (validationData.size() != 0 && !validationData.containsKey("Transaction Status")) {
            logger.info("ERROR: Issue with Transaction Data being sent from API call ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationData);
        }

        if (validationData.containsKey("Transaction Status")) {
            logger.info("Transaction information Deleted successfully with status: " + validationData.get("Transaction Status"));
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Transaction information Deleted: " + validationData.get("Transaction Status"));
        }

        return ResponseEntity.status(HttpStatus.OK).body("Transaction information Deleted: " + validationData);
    }

}
