package com.example.springbootbackend.Service;


import com.example.springbootbackend.model.Transaction;
import com.example.springbootbackend.model.UserCacheData;
import com.example.springbootbackend.model.DTO.UserPersonalInfoDTO;
import com.example.springbootbackend.repository.TransactionRepository;
import com.example.springbootbackend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.springbootbackend.Service.Reddis.RedisUserCacheService;





@Service
public class DataSanitizationService  {


    @Autowired
    TransactionRepository TransacRepo;

    @Autowired
    UserRepository UserRepo;
    @Autowired
    RedisUserCacheService CachedTransaction = new RedisUserCacheService();
    


    public UserCacheData SanitizeData(UserCacheData UnCleanData,String ValidCacheCode, Transaction transaction){
         UserCacheData CleanedData= null;
        
        /*
         * Switch statement based on the code that is given back from the validation method
         * 
         * 
         * Case: Null
         *  - Build a new object from the start 
         *  
         * 
         * Case: TransactionSize 
         *      - update the list to include the latest transactions values from the DB
         *          o Make sure the data that is being passed is... 
         *              * Not already in the array
         * 
         * 
         *  Case: OutOfDatePersonalInformation
         *      - Check is needed to be done on the information that is stored in the case to make sure that it is up to date
         *      - Need to also update the last update line in the object
         *              
         * 
         */

         switch (ValidCacheCode) {
            case "Null":
                CleanedData = FullBuild(UnCleanData,transaction.getUserId());
                break;

            case "OutOfDatePersonalInformation":
                CleanedData = PersonalInfoCleanUp(UnCleanData,transaction);
                break;
                
            case "TransactionSize":
                CleanedData = TransactionCleanUp(UnCleanData,transaction.getUserId());
         
            default:
                break;
         }

        CachedTransaction.saveUserCache(transaction.getUserId().toString(), CleanedData);
        return CleanedData;
    }


     private UserCacheData FullBuild(UserCacheData UnCleanData,UUID UserID){
        UserCacheData CleanedData = new UserCacheData(UnCleanData);
        
        //Get the Transactions from the Database
        CleanedData.setTransactions(TransacRepo.UserLastNTransactions(UserID, 10));

        Optional<UserPersonalInfoDTO> personalInfoOpt = UserRepo.getUserPersonalInfo(UserID);

        // Get the Personal Information from the Database
        personalInfoOpt.ifPresent(personalInfo -> {
            CleanedData.setAddress(personalInfo.getAddress());
            CleanedData.setUserId(personalInfo.getUserId());
            CleanedData.setEmail(personalInfo.getEmail());
            CleanedData.setRole(personalInfo.getRole());
            CleanedData.setUserName(personalInfo.getUsername());
            CleanedData.setPhone_number(personalInfo.getPhone_number());
            CleanedData.setName(personalInfo.getName());
        });
        // Update the LastUpdated 
        CleanedData.setLastUpdated(LocalDateTime.now().toString());


        return CleanedData;
     }


     private UserCacheData TransactionCleanUp(UserCacheData UnCleanData,UUID UserID){
        UserCacheData CleanedData = new UserCacheData(UnCleanData);
        TransacRepo.UserLastNTransactions(UserID, 10)
            .forEach((Transaction transaction) -> {
                if (!CleanedData.getTransactions().stream()
                        .anyMatch(t -> t.getTransactionId().equals(transaction.getTransactionId()))) {
                    CleanedData.getTransactions().add(transaction);
                }
            });

        return CleanedData;
     }
    


     private UserCacheData PersonalInfoCleanUp(UserCacheData UnCleanData, Transaction transaction){
        UserCacheData CleanedData = new UserCacheData(UnCleanData);
        UUID UserID = transaction.getUserId();
        
        Optional<UserPersonalInfoDTO> personalInfoOpt = UserRepo.getUserPersonalInfo(UserID);
        personalInfoOpt.ifPresent(personalInfo -> {
            CleanedData.setAddress(personalInfo.getAddress());
            CleanedData.setEmail(personalInfo.getEmail());
            CleanedData.setRole(personalInfo.getRole());
            CleanedData.setUserName(personalInfo.getUsername());
            CleanedData.setUserId(personalInfo.getUserId());
            CleanedData.setPhone_number(personalInfo.getPhone_number());
            CleanedData.setName(personalInfo.getName());
        });


        return CleanedData;
     }
    
    
}
