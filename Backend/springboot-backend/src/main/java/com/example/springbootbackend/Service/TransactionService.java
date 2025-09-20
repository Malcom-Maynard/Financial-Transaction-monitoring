package com.example.springbootbackend.Service;


import com.example.springbootbackend.model.Transaction;
import com.example.springbootbackend.model.UserCacheData;
import com.example.springbootbackend.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.springbootbackend.Service.Reddis.RedisUserCacheService;
import com.example.springbootbackend.Service.rabbitmq.MessageSender;


@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepo;  

    @Autowired
    private ValidationService validSer;

    @Autowired
    private MessageSender messageSender;

    @Autowired    
    private RedisUserCacheService cacheService;

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final ObjectMapper mapper = new ObjectMapper();

    // Method to create a new transaction
    public AbstractMap<String, Object> createTransaction(Transaction transaction) throws JsonProcessingException {
        String json = mapper.writeValueAsString(transaction);
        logger.info("createTransaction - Data from Transaction: " + json);

        AbstractMap<String, Object> validationMessages = validSer.validateTransactionInfomation(transaction);

        if (validationMessages.isEmpty()) {
            messageSender.sendMessageIN(transaction);

            // Poll for up to 5 seconds, checking every 0.5 seconds
            //Done this way to allow for the async processing of the transaction to be completed 
            int maxAttempts = 10;
            int attempt = 0;
            while (attempt < maxAttempts) {
                UserCacheData cachedData = cacheService.getUserCache(transaction.getUserId().toString());
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

        AbstractMap<String, Object> ValidationMessages = validSer.VaildateTransactionGet(transactionID,transaction);
        
        // Save user to the database via the repository

        //Pull the transaction from the cache if its there or from the Database if its not
        if (ValidationMessages.size()==0){

            //Pull from Cache
            Transaction transactionData = cacheService.getTransactionById(transaction.getUserId().toString(), transactionID.toString());

            if(transactionData != null){
                String json = mapper.writeValueAsString(transactionData);
                logger.info("Transaction Data found in Cache: " + json);
                ValidationMessages.put("Transaction Data", transactionData);
                return ValidationMessages;
            }

            //Not found in cache, checking the Database

            else{
                Optional<Transaction> transactionDataDB = transactionRepo.findById(transactionID);
                if(transactionDataDB.isPresent()){
                    String json = mapper.writeValueAsString(transactionDataDB.get());
                    logger.info("Transaction Data found in Database: " + json);
                    ValidationMessages.put("Transaction Data", transactionDataDB.get());
                    return ValidationMessages;
                }
                else{
                    logger.error("No Transaction Data found in Database for Transaction ID: " + transactionID);
                    ValidationMessages.put("Error","No Transaction Data found for Transaction ID: " + transactionID);
                    return ValidationMessages;
                }
                
            }
                
            

            
            


            }
            logger.error("Valid(Get) : no issue validating transaction details ");

            
        

       

        
        return ValidationMessages;
        
        
    }

    public AbstractMap<String, Object> updateTransaction(UUID transactionID, Transaction updatedTransaction) {
        AbstractMap<String, Object> validationMessages = new HashMap<>();

        Optional<Transaction> existingOpt = transactionRepo.findById(transactionID);
        if (existingOpt.isEmpty()) {
            validationMessages.put("Transaction Status", "Transaction not found");
            return validationMessages;
        }

        Transaction existing = existingOpt.get();

        // Update fields
        existing.setAmount(updatedTransaction.getAmount());
        existing.setLocation(updatedTransaction.getLocation());
        existing.setStatus(updatedTransaction.getStatus());
        existing.setTransaction_date(updatedTransaction.getTransaction_date());

        transactionRepo.save(existing);

        // Update cache
        cacheService.updateTransaction(existing.getUserId().toString(), existing);

        validationMessages.put("Transaction Status", "Transaction updated successfully");
        validationMessages.put("Transaction Data", existing);
        return validationMessages;
    }

    public AbstractMap<String, Object> deleteTransaction(UUID transactionID, Transaction transaction) {
        AbstractMap<String, Object> validationMessages = new HashMap<>();

        Optional<Transaction> existingOpt = transactionRepo.findById(transactionID);
        if (existingOpt.isEmpty()) {
            validationMessages.put("Transaction Status", "Transaction not found");
            return validationMessages;
        }

        Transaction existing = existingOpt.get();
        transactionRepo.deleteById(transactionID);

        // Remove from cache
        cacheService.removeTransactionById(existing.getUserId().toString(), transactionID.toString());

        validationMessages.put("Transaction Status", "Transaction deleted successfully");
        return validationMessages;
    }

    
}
