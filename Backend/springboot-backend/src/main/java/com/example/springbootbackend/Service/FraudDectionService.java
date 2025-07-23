package com.example.springbootbackend.Service;


import com.example.springbootbackend.model.Alerts;
import com.example.springbootbackend.model.Transaction;
import com.example.springbootbackend.model.User;
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
import com.example.springbootbackend.Service.Reddis.RedisTransactionArrayService;
import com.example.springbootbackend.Service.rabbitmq.MessageSender;
import com.example.springbootbackend.model.User;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.plaf.synth.SynthEditorPaneUI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class FraudDectionService {
    /*
     * Fraud dections rules
     *  Check the location of the purchase
     *      - High risk location: 
     *          1. Toronto
     *          2. Edmonton
     *          3. Downtown Eastside, Vancouver, BC
     *          4. Winnipeg, Manitoba       
     *              O Lord Selkirk Park
     *              O South Portage
     *          5. Red Deer, Alberta
     *          6. Kelowna, British Columbia
     * 
     * 
     *      - Going to add a location to the User account,
     *          - If the user location of the transaction is within the city it will be fine
     *              - Check would need to be done on the amount coming in
     *          - If the location within an high risk area or internaltionally 
     *              - if the amaount spent is over the average max payment amount
     *                  -FRAUD
     * 
     * - Amount of the transaction: 
     *          1. make a range of the lowest 3 transactions and highest 3 transactions
     *              - If the value of the transaction is out side of the range, then it will trigger an alert
     *              - 
     * 
     * 
     * 
     * 
     * 
     */
     ArrayList <String> HighRiskLocations =  Stream.of("Toronto, ON","Edmonton, AB","Downtown Eastside, BC" , "Red Deer, AB","Kelowna, BC","South Portage, MB","Lord Selkirk Park, MB").collect(Collectors.toCollection(ArrayList::new));
                     

    @Autowired
    UserRepository UserDBRepo;

    
    @Autowired
    TransactionRepository TransactionDBRepo;

   @Autowired
   TransactionCache TransactionCacheReddis;


    @Autowired
    AlertsRepository AlertsDBRepo;


   @Autowired    
   RedisTransactionArrayService CahceTransaction;
   


    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

     public Boolean AnaylizeTransaction ( Transaction transaction){
        Boolean fraudulent= false;

        Boolean inRange = true;
        Boolean inHighRiskArea =false;

        String TransactionLocation = transaction.getLocation();
        
        String TransactionCity = TransactionLocation.split(",")[1];
        String TransactionProvince = ProvinceShortFormConversion(TransactionLocation.split(",")[2].trim());
        
        Alerts alerts= new Alerts();

      //CACHE THIS DATA INTO REDDIS
        String UserLocation = UserDBRepo.GetUsersAddress(transaction.getUserId());

        
      
        String UserLocationFormated = UserLocation.split(",")[1] +", "+ProvinceShortFormConversion(UserLocation.split(",")[2]);
        String TransactionLocationFormated = TransactionCity+", "+TransactionProvince;
  

         logger.info("AnaylizeTransaction - In high risk : "+HighRiskLocations.contains(TransactionLocationFormated.trim())); 
        if(HighRiskLocations.contains(TransactionLocationFormated) && !TransactionLocationFormated.equals(UserLocationFormated)){inHighRiskArea= true; alerts.setAlert_reason("Transaction within high risk area");}


         // CACHE DATA INTO REDDIS
         //Uses Reddis to call the the the data for this
         

         List<Transaction> UserTransactions = new ArrayList<>(CahceTransaction.getList(transaction.getUserId().toString()));

         if (UserTransactions.size()<10){

            List<Transaction> UserTransactionsFromDB = TransactionDBRepo.UserLastNTransactions(transaction.getUserId(), Math.abs(UserTransactions.size()-10));

            
            UserTransactions.addAll(UserTransactionsFromDB);

            CahceTransaction.saveList(transaction.getUserId().toString(),UserTransactions);
         }
         

         //List<Transaction> UserTransactions = TransactionDBRepo.UserLastNTransactions(transaction.getUserId(), 10);
       
         


         
         double[] bounds = calculateBounds(UserTransactions, LocalDate.now(), 10.0);
         
         logger.info("AnaylizeTransaction - Lower Bound: "+bounds[0]); 
         logger.info("AnaylizeTransaction - Upper Bound: "+bounds[1]); 

         //If the Transaction is lower than lower bounds but under 75 dollars
         if(  (transaction.getAmount()< bounds[0] && transaction.getAmount()>50 ) || transaction.getAmount()>bounds[1] ){inRange=false; alerts.setAlert_reason("Out of predeicted transaction amount");}
        
        
        fraudulent = (!inRange ||  inHighRiskArea);
         logger.info("AnaylizeTransaction - inRange: "+inRange); 
         logger.info("AnaylizeTransaction - inHighRiskArea: "+inHighRiskArea); 
         logger.info("AnaylizeTransaction - fraudulent: "+fraudulent); 

        
        /* 
        Check if it is fraudulent or not
         - If fraudulent
            o add into Transaction table as 'Alerted' or 'Fruadlent'

            o Add into the 'alerts' table in postgres
               - Give some sort of reason
                  - out of projected range
                  - in bad place outside of city 
                  - etc
        
         - if NOT fraudulent
            o add into Database and give it a 'approved' status 
            o Use RabbitMQ to send data into a Service that will process it into Reddis

        
        
             
        */ 

        if(fraudulent){
         logger.info("AnaylizeTransaction - alertData: "+alerts.toString()); 
         alerts.setTransaction_id(transaction.getTransactionId());
         AlertsDBRepo.save(alerts);

         transaction.setStatus("DECLINED");
         

        } 

        else{
         transaction.setStatus("APPROVED");

         UserTransactions.add(transaction);
         CahceTransaction.saveList(transaction.getUserId().toString(), UserTransactions);

     

        }

        TransactionDBRepo.save(transaction);

        return fraudulent;
     }
      
    public static double[] calculateBounds(List<?> transactionsRaw, LocalDate targetDate, double marginPercent) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    

    ;



    // Safely convert LinkedHashMaps to Transaction objects
    List<Transaction> transactions = transactionsRaw.stream().map(obj -> objectMapper.convertValue(obj, Transaction.class)).collect(Collectors.toList());


    
        
        


          
    

    if (transactions.size() < 2) return new double[]{0, 0}; // Need at least 2 points

    double lowerBound = 0;
    double upperBound = 100;
    double avgTransactionAmount = 0;

    LocalDate baseDate = transactions.get(0).getTransaction_date().toLocalDate();
    SimpleRegression regression = new SimpleRegression();

    long daysBetweenLastTransaction = ChronoUnit.DAYS.between(targetDate, baseDate);

    for (Transaction tx : transactions) {
        long daysSinceStart = ChronoUnit.DAYS.between(baseDate, tx.getTransaction_date().toLocalDate());
        regression.addData(daysSinceStart, tx.getAmount());
        avgTransactionAmount += tx.getAmount();
    }

    avgTransactionAmount /= transactions.size();

    long targetDays = ChronoUnit.DAYS.between(baseDate, targetDate);
    double predicted = regression.predict(targetDays);
    double margin = predicted * (marginPercent / 100.0);

    lowerBound = predicted - margin;
    upperBound = predicted + margin;

    if (daysBetweenLastTransaction > 30 || lowerBound < 0) {
        lowerBound = 0;
    }

    if (daysBetweenLastTransaction > 30 || upperBound < 0) {
        upperBound = avgTransactionAmount + SampleStandardDev(transactions, avgTransactionAmount);
    }

    return new double[]{lowerBound, upperBound};
   }

     private String ProvinceShortFormConversion (String province){
      String ShortenProvince = province;

      switch (ShortenProvince.toLowerCase()) {
         case "ontario":
            ShortenProvince = "ON";
            break;

         case "alberta":
            ShortenProvince = "AB";
            break;

         case "brtish columbia":
            ShortenProvince = "AB";
            break;

         case "manitoba":
            ShortenProvince = "MB";
            break;

         case "new brunswick":
            ShortenProvince = "NB";
            break;

         case "newfoundland and labrador":
            ShortenProvince = "NL";
            break;

         case "nova scotia":
            ShortenProvince = "NS";
            break;

         case "prince edward island":
            ShortenProvince = "PEI";
            break;

         case "quebec":
            ShortenProvince = "QC";
            break;

         case "saskatchewan":
            ShortenProvince = "SK";
            break;
      
         default:
            break;
      }
      


      return ShortenProvince;
     }


      private static Double SampleStandardDev (List<Transaction> transactions , double mean){
      Double SD = 0.0;

      for(Transaction tx : transactions){

         SD+= Math.pow((tx.getAmount()-mean),2);

      }


      SD=Math.sqrt(SD/transactions.size());

      return SD;


     }
}    
