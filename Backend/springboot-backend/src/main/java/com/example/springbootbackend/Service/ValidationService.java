package com.example.springbootbackend.Service;

import com.example.springbootbackend.Service.rabbitmq.MessageSender;
import com.example.springbootbackend.model.User;
import com.example.springbootbackend.model.UserCacheData;
import com.example.springbootbackend.model.DTO.UserDTO;
import com.example.springbootbackend.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.springbootbackend.model.Transaction;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ValidationService {

    
    
    private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);
    private AbstractMap<String,Object> VaildationPackage = new HashMap <String,Object>();

    private AbstractMap<String,Object> TestingVaildationPackage = new HashMap <String,Object>();

    

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository; 
   
    
    public AbstractMap<String, Object> VaildateAddUserData (User user){
        
        /* 
        Backend Vaildation for User infomation

        O Name
            - check if it a name of Alpbabet characters 
            - No numbers
            - speical chars
            - not empty or null
            -
        
        O  Email
            - vaild Email format
            - make sure it is not already being used in by another user
            - not empty of null


        O Password
            - vaild password format as the front end
            - not empty or null

        O username
            - speical chars
            - not empty or null
            - make sure its not being used by another person

        O role
            - make sure its one of the two values 
                - Admin or user

        O PhoneNumber
            - speical chars other "-""
            - not empty or null
            - make sure its not being used by another person

        Going to send it back to the API like the following in a hashmap 
            Field: Issue
       */
        
            
        

        
        VaildPhoneNumber(user.getPhoneNumber());
        VaildEmail(user.getEmail());
        VaildName(user.getName());
        VaildPassword(user.getPassword());
        VaildRole(user.getRole());
        VaildUsername(user.getUsername());
        vaildateLocation(user.getAddress());
        
        logger.info(VaildationPackage.toString());
        


      
        return VaildationPackage;




           


    }


    public AbstractMap<String, Object> VaildateUserID (UUID UserID){

        VaildUserID(UserID);
        return TestingVaildationPackage;
    }

    public AbstractMap<String, Object> VaildatePutUserData (UserDTO user, UUID userID){

        /*
         * Vaildation needed for the put Operations
         *  - Check first if all the feilds are blank or not
         *      - If the feild isnt blank run the vaildation for that respected element
         *          -Any way what is the best way to do this??
         * 
         * 
         * 
         */

        VaildUserID(userID);

        if(TestingVaildationPackage.size()==0){


       
            if (user.getName()!=null)   {
                VaildName(user.getName());

            }

            if (user.getEmail()!=null){
                VaildEmail(user.getEmail());

            }

            if (user.getName()!=null){
                VaildName(user.getName());

            }


            if (user.getRole()!=null){
                VaildRole(user.getRole());

            }

            if (user.getUsername()!=null){
                VaildUsername(user.getUsername());

            }
        }


       



        


        return VaildationPackage;
    }

     public String vaildatingUserCache (UserCacheData CachedData){
        String Vaild = "GOOD!";


        /*
         * Checks on the Object
         * 
         * 1. check if the object is null
         *  a) Check if the transaction cache has atleast 10 values inside
         *  b) Make sure that data is not too old
         *      - Ex check that the data isnt more than 5 months old
         *      - Need to add something on the UserCahce data to update the last pulled date and also add in the feild
         */ 

        if(CachedData==null){
            logger.info("ValidationService: UserCacheData is null");
            return "Null";
        }

         if(CachedData==null){return "Null";};

         if(CachedData.getTransactions().size()!=10){return "TransactionSize";}

        LocalDateTime LastUpdated = LocalDateTime.parse(CachedData.getLastUpdated());
        LocalDateTime CurrentDateTime = LocalDateTime.now();
         Duration dur = Duration.between(LastUpdated, CurrentDateTime);

         if(dur.toDays()>152){
            return "OutOfDatePersonalInformation";
         }






        return Vaild;
    }

    public AbstractMap<String, Object> VaildateDeleteUserData (UUID CurrentUserID, UUID DeleteUserID){

        /*
         * Vaildation needed for the put Operations
         *  - Check if the UUCID is vaild.
         *      - Make sure the username of the person is an "Admin"
         *      - Make sure the person that is being removed is a user role
         */

        
         validateUserID(DeleteUserID, "Request URL");
         validateUserID(CurrentUserID, "Request Body");
        

         if(TestingVaildationPackage.isEmpty()){

            //Check the userID found in the URL 
            String Role = userRepository.GetUserRole(CurrentUserID);

                if (!Role.equals("Admin")){
                    VaildationPackage.put("userID",   "Non-Admins can not delelte users." );

                }

            

            
         }


       



        


        return VaildationPackage;
    }

    
    public AbstractMap<String, Object>  validateTransactionInfomation (Transaction transaction){

        /* 
        Backend Vaildation for User infomation

        O userID
            - check make sure the user ID is vaild
            - make sure that the USER_id exist in the system
        
        O Location
            -  Make sure that the location is a string        
         */

        
        VaildUserID(transaction.getUserId());
        vaildateLocation(transaction.getLocation());



        return VaildationPackage;
        
    }
    private Boolean VaildName (String Name){
        
        
        if(!Name.matches("^[a-zA-Z ]{1,50}$")){
            logger.info("Invaild Name: Name can only contain letters ie('John Smith')");
            VaildationPackage.put("Name", "Invaild Name: Name can only contain letters ie('John Smith')");
            
            return false;

        }
        return true;


    }

    public AbstractMap<String, Object> VaildateTransactionGet( UUID transactionID, Transaction transaction){
         /* 
        Backend Vaildation for Trssansaction infomation & if passed UserID

        O TransactionID
            - check make sure the TransactionID is vaild (UUID format)
            - make sure that the TransactionID exist in the system
        
        O UserID
            - Check if the UserID is not empty
                - check make sure the user ID is vaild
                - make sure that the USER_id exist in the system   
         */
        VaildTransactionID(transactionID.toString());
        validateUserID(transaction.getUserId(), "Request Body");
        
        return VaildationPackage;
    }


    private Boolean VaildTransactionID (String ID){
        String Pattern= "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        Boolean vaild = true;
         logger.info("Vaildating TransactionID: "+ID);
        if(!ID.matches(Pattern)){
            logger.info("Invaild TransactionID: TransactionID is not vaild");
            VaildationPackage.put("TransactionID", "Invaild TransactionID: TransactionID is not vaild");
            
            vaild = false;

        }
        else{
            
        }
        return vaild;
        
    }


    


    private Boolean VaildRole (String Role){
        ArrayList<String> vaildRoles = new ArrayList<>(Arrays.asList("User", "Admin", "TEST"));
        
        if(!(vaildRoles.contains(Role))){
            VaildationPackage.put("Role", "Invaild Role: Roles can be the following; User, Admin, TEST");
            
            return false;

        }
        return true;


    }


    private Boolean VaildUsername (String Username){
        Boolean vaild = true;
        if (!Username.trim().matches("^(?=.*[A-Za-z0-9!_-])[A-Za-z0-9!_-]{6,}$")){
            logger.info("Invaild Username: Username is not vaild");
            VaildationPackage.put("Username", "Invaild Username: Username is not vaild");
            
            vaild = false;

        }

        //Check on if username exist in the system
        String sql = String.format("SELECT Count(*) FROM public.\"user\" WHERE username='%s'",Username);
        logger.info("SQL: "+sql);
        Query query = entityManager.createNativeQuery(sql);
        logger.info("Vaildation Username Count: "+query.getResultList().get(0));
        
       

        Integer UsernameCount = Integer.parseInt(query.getResultList().get(0).toString());

        if (UsernameCount>1){


            if(VaildationPackage.containsKey("Username")){

                String Currentmsg = (String) VaildationPackage.get("Username");
                VaildationPackage.put("Username", Currentmsg+" | Username is already in use, please use another one.");
                logger.info("Username is already in use, please use another one");
                vaild=false;
                
            }

            else{
                VaildationPackage.put("Username", "Username is already in use, please use another one.");

            }
            
           
        }

        
        return vaild;

    }


    private Boolean VaildPassword (String Password){
        
        logger.info("Vaildating PAssword: "+Password);
        String newValue = Password.trim();

        if (!newValue.trim().matches("^(?=.*\\d.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{}|;:'\",.<>?/`~])[A-Za-z0-9!@#$%^&*()_+=\\-\\[\\]{}|;:'\",.<>?/`~]{6,}$")) {     
            
            logger.info("(VaildPassword)Invaild Password: Invaild Password: Password must follow the following: Made from numbers , characters and speical characters");
            VaildationPackage.put("Password", "Invaild Password: Password must follow the following: Made from numbers , characters and speical characters");
            
            return false;

        }
        return true;


    }

    private Boolean VaildPhoneNumber (String PhoneNumber){
        
        Boolean vaild = true;
        String FormattedPhoneNumber = PhoneNumber.replace("-", "");

        logger.info("FormatredPhone Number: "+FormattedPhoneNumber);
        
        if(!FormattedPhoneNumber.trim().matches("^(?:\\+1\\s?)?(?:\\(?\\d{3}\\)?[\\s.-]?)?\\d{3}[\\s.-]?\\d{4}$")){
            logger.info("Invaild Phone number: must follow sample format (EX: 000-000-0000)");
            VaildationPackage.put("PhoneNumber", "Invaild Phone number: must follow sample format (EX: 000-000-0000)");
            
            vaild= false;

        }

        //Check on if phonenumber exist in the system
        String sql = String.format("SELECT Count(*) FROM public.\"user\" WHERE phone_number='%s'",PhoneNumber);
        logger.info("SQL(Phone number count): "+sql);
        Query query = entityManager.createNativeQuery(sql);
        logger.info("Vaildation Username Count: "+query.getResultList().get(0));
        
       

        Integer UsernameCount = Integer.parseInt(query.getResultList().get(0).toString());

        if (UsernameCount>1){


            if(VaildationPackage.containsKey("PhoneNumber")){

                String Currentmsg = (String) VaildationPackage.get("PhoneNumber");
                VaildationPackage.put("PhoneNumber", Currentmsg+" | PhoneNumber is already in use, please use another one.");
                logger.info("PhoneNumber is already in use, please use another one");
                vaild=false;
                
            }

            else{
                VaildationPackage.put("PhoneNumber", "PhoneNumber is already in use, please use another one.");

            }
            

            
           
        }

        return vaild;


    }

    private Boolean VaildEmail (String email){
        Boolean vaild=true;
        logger.info(email);
        if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){

            VaildationPackage.put("E-mail", "Invaild Email format must follow format XXXXX@XXXX@.XXX");
            vaild= false;
        }
        
        String sql = String.format("SELECT Count(*) FROM public.\"user\" WHERE email='%s'",email);
        logger.info("SQL: "+sql);
        Query query = entityManager.createNativeQuery(sql);
        logger.info("Vaildation Email Count: "+query.getResultList().get(0));
        
       

        Integer EmailCount = Integer.parseInt(query.getResultList().get(0).toString());

        if (EmailCount>1){

            if(VaildationPackage.containsKey("E-mail")){

                String Currentmsg = (String) VaildationPackage.get("E-mail");
                VaildationPackage.put("E-mail", Currentmsg+" | E mail is already in use, please use another one");
                
                
            }


            else{
                VaildationPackage.put("E-mail", "E mail is already in use, please use another one");


            }

            
            vaild= false;
        }



        

        
        return vaild;


    }

    private void validateUserID(UUID userID, String source) {
        VaildUserID(userID);
    
        if (!TestingVaildationPackage.isEmpty()) {
            String currentMsg = (String) VaildationPackage.getOrDefault("userID", "");
            VaildationPackage.put("userID", currentMsg + "(Invalid UserID found in " + source + ").");
        }

        CheckUserIDExistence(userID);
    }

    private void vaildateLocation (String Location){

         if (!Location.toString().trim().matches("^.+?,\\s*.+?,\\s*.+?,\\s*[A-Za-z]\\d[A-Za-z]\\s?\\d[A-Za-z]\\d,\\s*.+$")) {  
               
            logger.info("Invaild Location: please send a Location data ");
            TestingVaildationPackage.put("Location", "Invaild format of Location, please follow the following format City, 'State/Province, Country, Area Code' ");
            
            

        }
        

    }
    
    private void CheckUserIDExistence(UUID userID) {
        
        Integer ReturnedRows = userRepository.CheckUserID(userID);
        logger.info("NUMEBR OF ROWS RETURNED: "+ReturnedRows.toString());
        if (ReturnedRows>1){

            //If there is Data already in vaildation package
            if (TestingVaildationPackage.get("userID") != null ) {
                String currentMsg = (String) VaildationPackage.getOrDefault("userID", "");
                VaildationPackage.put("userID", currentMsg + "UserID not found in Backend");
            }


        }

         
       
    }
    
    
    
    private Boolean VaildUserID (UUID userID){
        logger.info("Vaildating userID: ",userID);
        

        if (!userID.toString().matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {  
               
            logger.info("Invaild UserId: please send a vaild UserID ");
            TestingVaildationPackage.put("userID", "Invaild UserId: please send a vaild UserID ");
            
            return false;

        }
        return true;

    }


   
    






}
