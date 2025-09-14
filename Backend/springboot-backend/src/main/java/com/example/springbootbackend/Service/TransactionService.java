package com.example.springbootbackend.Service;


import com.example.springbootbackend.model.Transaction;
import com.example.springbootbackend.model.User;
import com.example.springbootbackend.model.UserCacheData;
import com.example.springbootbackend.model.DTO.UserDTO;
import com.example.springbootbackend.repository.TransactionRepository;
import com.example.springbootbackend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
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
import com.example.springbootbackend.Service.Reddis.RedisUserCacheService;
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

    @Autowired    
    private RedisUserCacheService CahceTransaction;

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final ObjectMapper mapper = new ObjectMapper();

    // Method to create a new transaction
    public AbstractMap<String, Object> createTransaction(Transaction transaction) throws JsonProcessingException {
        String json = mapper.writeValueAsString(transaction);
        logger.info("createTransaction - Data from Transaction: " + json);

        AbstractMap<String, Object> validationMessages = ValdSer.validateTransactionInfomation(transaction);

        if (validationMessages.isEmpty()) {
            messageSender.sendMessageIN(transaction);

            // Poll for up to 5 seconds, checking every 0.5 seconds
            int maxAttempts = 10;
            int attempt = 0;
            while (attempt < maxAttempts) {
                UserCacheData cachedData = CahceTransaction.getUserCache(transaction.getUserId().toString());
                List<Transaction> transactionUserData = (cachedData != null) ? cachedData.getTransactions() : null;

                if (transactionUserData != null && !transactionUserData.isEmpty()) {
                    Transaction latestTransaction = transactionUserData.get(transactionUserData.size() - 1);
                    if (!"PENDING".equals(latestTransaction.getStatus())) {
                        // Transaction processed
                        String latestTransactionHour = latestTransaction.getTransaction_date().toString().substring(0, 13);
                        String currentHour = LocalDateTime.now().toString().substring(0, 13);

                        boolean isSameHour = latestTransactionHour.equals(currentHour);
                        boolean isSameAmount = latestTransaction.getAmount().equals(transaction.getAmount());

                        if (isSameHour && isSameAmount) {
                            if (!"DECLINED".equals(latestTransaction.getStatus())) {
                                validationMessages.put("Transaction Status", "Transaction Completed Successfully");
                            } else {
                                validationMessages.put("Transaction Status", "Transaction Failed due to possible fraudulent activity");
                            }
                        } else {
                            validationMessages.put("Transaction Status", "Transaction is still being processed, please try again in a few seconds using the transaction ID: " + transaction.getTransactionId());
                        }
                        break;
                    }
                }
                try {
                    Thread.sleep(500); // Wait 0.5 seconds before next check
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Thread was interrupted during polling", e);
                    break;
                }
                attempt++;
            }
        }

        return validationMessages;
        
        
    }

    public AbstractMap<String,Object> getTransaction(UUID transactionID, Transaction transaction) throws JsonProcessingException{
        // Perform business logic, such as validation or other processing if needed
        logger.info("TransactionID being passed: "+transactionID);

        AbstractMap<String, Object> VaildationMessages = ValdSer.VaildateTransactionGet(transactionID,transaction);
        
        // Save user to the database via the repository
        if (VaildationMessages.size()==0){

            logger.error("Vaild : no isue vaildatting transaction detials ");
            
        }

        else{
            logger.error("Error: issue vaildatting transaction detials ");
            
        }

        
        return VaildationMessages;
        
        
    }

    
}
