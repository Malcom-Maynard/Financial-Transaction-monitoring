package com.example.springbootbackend.Service;

import com.example.springbootbackend.model.User;
import com.example.springbootbackend.model.DTO.UserDTO;
import com.example.springbootbackend.rabbitmq.MessageSender;
import com.example.springbootbackend.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VaildationService {

    
    
    private static final Logger logger = LoggerFactory.getLogger(VaildationService.class);
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

            Integer RowsReturned =  userRepository.CheckUserID(DeleteUserID);


            

            
         }


       



        


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
        if (!Username.matches("^(?=.*[A-Za-z0-9!_-])[A-Za-z0-9!_-]{6,}$")){
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

        if (UsernameCount>0){


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
        
        logger.info("Vaildating PAssword");
        
        if (!Password.matches("^(?=.*\\d.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{}|;:'\",.<>?/`~])[A-Za-z0-9!@#$%^&*()_+=\\-\\[\\]{}|;:'\",.<>?/`~]{6,}$")) {     
            
            logger.info("Invaild Password: Invaild Password: Password must follow the following: Made from numbers , characters and speical characters");
            VaildationPackage.put("Password", "Invaild Password: Password must follow the following: Made from numbers , characters and speical characters");
            
            return false;

        }
        return true;


    }

    private Boolean VaildPhoneNumber (String PhoneNumber){
        
        Boolean vaild = true;
        String FormattedPhoneNumber = PhoneNumber.replace("-", "");

        logger.info("FormatredPhone Number: "+FormattedPhoneNumber);
        
        if(!FormattedPhoneNumber.matches("^(?:\\+1\\s?)?(?:\\(?\\d{3}\\)?[\\s.-]?)?\\d{3}[\\s.-]?\\d{4}$")){
            logger.info("Invaild Phone number: must follow sample format (EX: 000-000-0000)");
            VaildationPackage.put("PhoneNumber", "Invaild Phone number: must follow sample format (EX: 000-000-0000)");
            
            vaild= false;

        }

        //Check on if phonenumber exist in the system
        String sql = String.format("SELECT Count(*) FROM public.\"user\" WHERE phone_number='%s'",PhoneNumber);
        logger.info("SQL: "+sql);
        Query query = entityManager.createNativeQuery(sql);
        logger.info("Vaildation Username Count: "+query.getResultList().get(0));
        
       

        Integer UsernameCount = Integer.parseInt(query.getResultList().get(0).toString());

        if (UsernameCount>0){


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

        if (EmailCount>0){

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
    }


    private void CheckUserIDExistence(UUID userID, String source) {
        
        Integer ReturnedRows = userRepository.CheckUserID(userID);
    
        if (!TestingVaildationPackage.isEmpty() ) {
            String currentMsg = (String) VaildationPackage.getOrDefault("userID", "");
            VaildationPackage.put("userID", currentMsg + "(Invalid UserID found in " + source + ").");
        }
    }
    
    private Boolean VaildUserID (UUID userID){
        logger.info("Vaildating userID");
        

        if (!userID.toString().matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {  
               
            logger.info("Invaild UserId: please send a vaild UserID ");
            TestingVaildationPackage.put("userID", "Invaild UserId: please send a vaild UserID ");
            
            return false;

        }
        return true;

    }
}
