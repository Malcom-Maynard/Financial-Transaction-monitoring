package com.example.springbootbackend.Service;

import com.example.springbootbackend.model.User;
import com.example.springbootbackend.model.DTO.UserDTO;
import com.example.springbootbackend.repository.UserRepository;

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
public class UserService {

    @Autowired
    private UserRepository userRepository;  // Injecting the Repository layer

    @Autowired
    private VaildationService ValdSer;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    // Method to create a new user
    public AbstractMap<String,Object> createUser(User user) {
        // Perform business logic, such as validation or other processing if needed
        logger.info("Data from user: "+user.printData());

        AbstractMap<String,Object> VaildationMessages = ValdSer.VaildateAddUserData(user);
        // Save user to the database via the repository
    
        if (VaildationMessages.size()==0){
            userRepository.save(user);

        }

        else{

            logger.error("There was an error in the processing of data");
        }

        
        return VaildationMessages;
        
        
    }


    public AbstractMap<String,Object> GetUser(UUID userID) {
        // Perform business logic, such as validation or other processing if needed
        logger.info("UserID being passed: "+userID);

        AbstractMap<String, Object> VaildationMessages = ValdSer.VaildateUserID(userID);
        // Save user to the database via the repository
        

        if (VaildationMessages.size()==0){

            logger.error("Vaild UserID, going to get infomation from backend ");
            
        }

        else{
            logger.error("Issue vaildating the UserID");
            
        }

        
        return VaildationMessages;
        
        
    }



    public AbstractMap<String,Object> UpdateUser(UUID userID,UserDTO UserUpdateInfo) {
        // Perform business logic, such as validation or other processing if needed


        
        logger.info("UserID being passed: "+userID);
        logger.info("New Person Data: "+UserUpdateInfo.getAddress());

        AbstractMap<String, Object> VaildationMessages = ValdSer.VaildatePutUserData(UserUpdateInfo,userID);
        
        //Call the DB and update the DB with the infomation that is in the body of the request
        /* Name
         * Phone Number
         * E-mail
         * Username
         */

        if (VaildationMessages.size()==0){

            logger.error("Vaild UserID, going to get infomation from backend ");
            
        }

        else{
            logger.error("Issue vaildating the UserID");
            
        }

        
        return VaildationMessages;
        
        
    }



    public AbstractMap<String,Object> DeleteUser(UUID UserIDDeletedUser,UUID CurrentUser) {
        // Perform business logic, such as validation or other processing if needed


        
        logger.info("UserID being passed: "+UserIDDeletedUser);

        AbstractMap<String, Object> VaildationMessages = ValdSer.VaildateDeleteUserData(CurrentUser,UserIDDeletedUser);
        

        if (VaildationMessages.size()==0){

            logger.error("Vaild UserID, going to get infomation from backend ");
            userRepository.DeleteUser(UserIDDeletedUser);
            
        }

        else{
            logger.error("Issue vaildating the UserID");
            
        }

        
        return VaildationMessages;
        
        
    }


    
}
