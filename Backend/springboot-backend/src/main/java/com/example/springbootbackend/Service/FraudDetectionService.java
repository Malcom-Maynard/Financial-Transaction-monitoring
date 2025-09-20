package com.example.springbootbackend.Service;

import com.example.springbootbackend.model.*;
import com.example.springbootbackend.repository.*;
import com.example.springbootbackend.Service.Reddis.RedisUserCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FraudDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    //Based on research of high risk locations in Canada for fraud, could be expanded in the future if needed
    private final List<String> HighRiskLocations = Stream.of(
            "Toronto, ON", "Edmonton, AB", "Downtown Eastside, BC",
            "Red Deer, AB", "Kelowna, BC", "South Portage, MB", "Lord Selkirk Park, MB"
    ).collect(Collectors.toList());

    @Autowired
    private TransactionRepository TransactionDBRepo;

    @Autowired
    private AlertsRepository AlertsDBRepo;

    @Autowired
    private ValidationService validation;

    @Autowired
    private RedisUserCacheService CachedTransaction;

    @Autowired
    private DataSanitizationService DataSanitize;

    public Boolean AnalyzeTransaction(Transaction transaction) {

        boolean fraudulent = false;
        boolean inRange = true;
        boolean inHighRiskArea = false;
        String TransactionLocation = transaction.getLocation();
        String TransactionCity = TransactionLocation.split(",")[1];
        String TransactionProvince = ProvinceShortFormConversion(TransactionLocation.split(",")[2].trim());
        Alerts alerts = new Alerts();
        String UserLocation = "";

        UserCacheData CachedInfo = CachedTransaction.getUserCache(transaction.getUserId().toString());
        String ValidCache = validation.vaildatingUserCache(CachedInfo);


        logger.info("validation Message from validation of Cached Data" + ValidCache);

        //"GOOD!" means the cache is valid and can be used as is, if it does not it will need to clean to be processed further
        if (!ValidCache.equalsIgnoreCase("GOOD!")) {
            CachedInfo = DataSanitize.SanitizeData(CachedInfo, ValidCache, transaction);
        }

        logger.info("AnalyzeTransaction - CachedInfo: " + CachedInfo);

        //Formatting of the location to be used in the processing
        UserLocation = CachedInfo.getAddress();
        String UserLocationFormatted = UserLocation.split(",")[1] + ", " + ProvinceShortFormConversion(UserLocation.split(",")[2]);
        String TransactionLocationFormatted = TransactionCity + ", " + TransactionProvince;

        logger.info("AnalyzeTransaction - In high risk : " + HighRiskLocations.contains(TransactionLocationFormatted.trim()));
        if (HighRiskLocations.contains(TransactionLocationFormatted) && !TransactionLocationFormatted.equals(UserLocationFormatted)) {
            inHighRiskArea = true;
            alerts.setAlert_reason("Transaction within high risk area");
        }

        // Get the user's last 10 transactions from cache, if less than 10 get from DB
        //The value of 10 was used as it worked for the small amount of data that is being tested for the project
        // For future uses, the magic N number will need to be expanded and made dynamic based on the users transaction history
        List<Transaction> UserTransactions = new ArrayList<>(CachedTransaction.getUserCache(transaction.getUserId().toString()).getTransactions());

        logger.info("AnalyzeTransaction - UserTransactions from Cache size: " + UserTransactions.size());

        if (UserTransactions.size() < 10) {
            Set<UUID> existingTransactionIds = UserTransactions.stream()
                    .map(Transaction::getTransactionId)
                    .collect(Collectors.toSet());

            List<Transaction> UserTransactionsFromDB = TransactionDBRepo.UserLastNTransactions(
                    transaction.getUserId(), Math.abs(UserTransactions.size() -10)
            ).stream()
                    .filter(tx -> !existingTransactionIds.contains(tx.getTransactionId()) && tx.getStatus().equals("APPROVED"))
                    .collect(Collectors.toList());

            UserTransactions.addAll(UserTransactionsFromDB);
            CachedTransaction.updateUserTransactions(transaction.getUserId().toString(), UserTransactions);
        }

        logger.info("Transactions being used - All Transactions: ");
        for (Transaction tx : UserTransactions) {
            logger.info("Transaction ID: " + tx.getTransactionId()
                    + ", Amount: " + tx.getAmount()
                    + ", Date: " + tx.getTransaction_date()
                    + ", Location: " + tx.getLocation()
                    + ", Status: " + tx.getStatus());
        }

        //A liner regression model is used to predict the next transaction amount based on the users last 10 transactions
        // A margin of 48% was used based on testing with the small data set, this will need to be adjusted based on further testing with a larger data set
        double[] bounds = calculateBounds(UserTransactions, LocalDate.now(), 48.0);

        logger.info("AnalyzeTransaction - Lower Bound: " + bounds[0]);
        logger.info("AnalyzeTransaction - Upper Bound: " + bounds[1]);

        //Problem found during testing if the users don't have enough data, causes the model to return NaN. Using average as a fallback
        if (UserTransactions.size() < 2) {
            logger.info("AnalyzeTransaction - Not enough transactions to make a prediction, using 0 as bounds");
            bounds[0] = 0;
            bounds[1] = 200;
        }

        
        
        //The inRange check also has a lower limit of 150, this is to allow for small transactions to be processed without being flagged
        if (transaction.getAmount() > bounds[1] || (transaction.getAmount() < bounds[0] && transaction.getAmount() > 150)) {
            inRange = false;
            alerts.setAlert_reason("Out of predicted transaction amount");
        }
        
        

        fraudulent = (!inRange || inHighRiskArea);
        logger.info("AnalyzeTransaction - inRange: " + inRange);
        logger.info("AnalyzeTransaction - inHighRiskArea: " + inHighRiskArea);
        logger.info("AnalyzeTransaction - fraudulent: " + fraudulent);

        if (fraudulent) {
            logger.info("AnalyzeTransaction - alertData: " + alerts);
            alerts.setTransaction_id(transaction.getTransactionId());
            AlertsDBRepo.save(alerts);
            transaction.setStatus("DECLINED");
            // Send the message to RabbitMQ to alert the user that their transaction was declined
        } else {
            transaction.setStatus("APPROVED");
            UserTransactions.add(transaction);
        }

        // Add the transaction to the cache and the DB
        CachedTransaction.addTransaction(transaction.getUserId().toString(), transaction);
        TransactionDBRepo.save(transaction);

        return fraudulent;
    }

    public static double[] calculateBounds(List<?> transactionsRaw, LocalDate targetDate, double marginPercent) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        List<Transaction> transactions = transactionsRaw.stream()
                .map(obj -> objectMapper.convertValue(obj, Transaction.class))
                .collect(Collectors.toList());

        // Once again, will trigger the average math fall back if there are less than 2 points
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
        logger.info("Target days since base date: " + targetDays);
        double predicted = regression.predict(targetDays);

        // Handle NaN prediction
        if (Double.isNaN(predicted)) {
            logger.warn("Regression prediction returned NaN. Using average transaction amount as fallback.");
            predicted = avgTransactionAmount;
        }

        double margin = predicted * (marginPercent / 100.0);

        logger.info("Predicted amount: " + predicted + ", Margin: " + margin);
        lowerBound = predicted - margin;
        upperBound = predicted + margin;

        //Issue with the model if the user has not had a transaction in over 30 days, the bounds become invalid
        //Changes to the lower and the upper bound to adjust for this.
        if (daysBetweenLastTransaction > 30 || lowerBound < 0) {
            lowerBound = 0;
        }

        if (daysBetweenLastTransaction > 30 || upperBound < 0) {
            upperBound = avgTransactionAmount + SampleStandardDev(transactions, avgTransactionAmount);
        }

        return new double[]{lowerBound, upperBound};
    }

     private String ProvinceShortFormConversion(String provinceFull) {
        return switch (provinceFull) {
            case "Ontario" -> "ON";
            case "Alberta" -> "AB";
            case "British Columbia" -> "BC";
            case "Manitoba" -> "MB";
            case "Quebec" -> "QC";
            case "Saskatchewan" -> "SK";
            case "Nova Scotia" -> "NS";
            case "New Brunswick" -> "NB";
            case "Newfoundland and Labrador" -> "NL";
            case "Prince Edward Island" -> "PE";
            case "Northwest Territories" -> "NT";
            case "Yukon" -> "YT";
            case "Nunavut" -> "NU";
            default -> provinceFull; // Return the original if no match is found
        };
    }

    private static Double SampleStandardDev(List<Transaction> transactions, double mean) {
        Double SD = 0.0;
        for (Transaction tx : transactions) {
            SD += Math.pow((tx.getAmount() - mean), 2);
        }
        SD = Math.sqrt(SD / transactions.size());
        return SD;
    }
}
