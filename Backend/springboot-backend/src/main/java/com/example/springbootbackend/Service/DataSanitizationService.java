package com.example.springbootbackend.Service;
import com.example.springbootbackend.model.Alerts;
import com.example.springbootbackend.model.Transaction;
import com.example.springbootbackend.model.User;
import com.example.springbootbackend.model.UserCacheData;
import com.example.springbootbackend.model.DTO.UserDTO;
import com.example.springbootbackend.repository.AlertsRepository;
import com.example.springbootbackend.repository.TransactionRepository;
import com.example.springbootbackend.repository.UserRepository;
import com.example.springbootbackend.repository.Reddis.TransactionCache;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.springbootbackend.model.Alerts;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.springbootbackend.Service.VaildationService;
import com.example.springbootbackend.Service.Reddis.RedisUserCacheService;
import com.example.springbootbackend.Service.Reddis.RedisUserCacheService;
import com.example.springbootbackend.Service.rabbitmq.MessageSender;
import com.example.springbootbackend.model.User;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.plaf.synth.SynthEditorPaneUI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@Service
public class DataSanitizationService  {




    public UserCacheData SanatizeData(UserCacheData UnCleanData){

        //need to change this, if the object is null, it is going to through an error during the making of the object
        // I could adjust the constutor to allow for this using the tenary operator or just do a check on the whole object before had
        // And If null --> make base object
        // If not null just follow as needed
        UserCacheData CleanedData = new UserCacheData(UnCleanData);


        /*
         * Switch stament based on the code that is given back from the vaildation method
         * 
         * 
         * Case: Null
         *  - Build a new object from the start 
         *  
         * 
         * Case: TransactionSize 
         *      - update the list to inculde the lastest transactions values from the DB
         *          o Make sure the data that is being passed is... 
         *              * Not already in the array
         * 
         * 
         *  Case: OutOfDatePersonalInfomation
         *      - Check is needed to be done on the infomation that is stored in the case to make sure that it is up to date
         *      - Need to also update the last update line in the object
         *              
         * 
         */










        
        return CleanedData;
    }

    
}
