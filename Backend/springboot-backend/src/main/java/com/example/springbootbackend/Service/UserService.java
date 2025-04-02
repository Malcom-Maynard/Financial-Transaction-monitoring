package com.example.springbootbackend.Service;

import com.example.springbootbackend.model.User;
import com.example.springbootbackend.repository.UserRepository;

import java.util.AbstractMap;
import java.util.Dictionary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.springbootbackend.Service.VaildationService;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;  // Injecting the Repository layer

    @Autowired
    private VaildationService ValdSer;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    // Method to create a new user
    public AbstractMap<String,String> createUser(User user) {
        // Perform business logic, such as validation or other processing if needed
        logger.info("Data from user: "+user.printData());

        AbstractMap<String,String> VaildationMessages = ValdSer.VaildateData(user);
        // Save user to the database via the repository
        

        if (VaildationMessages.size()==0){
            userRepository.save(user);

        }

        else{

            logger.error("There was an error in the processing of data");
        }

        
        return VaildationMessages;
        
        
    }


    
}
