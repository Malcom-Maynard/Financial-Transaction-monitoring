package com.example.springbootbackend.Service;

import com.example.springbootbackend.model.*;
import com.example.springbootbackend.model.DTO.UserDTO;
import com.example.springbootbackend.repository.*;
import com.example.springbootbackend.repository.Reddis.TransactionCache;
import com.example.springbootbackend.Service.Reddis.RedisUserCacheService;
import com.example.springbootbackend.Service.rabbitmq.MessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FraudDectionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final List<String> HighRiskLocations = Stream.of(
            "Toronto, ON", "Edmonton, AB", "Downtown Eastside, BC",
            "Red Deer, AB", "Kelowna, BC", "South Portage, MB", "Lord Selkirk Park, MB"
    ).collect(Collectors.toList());

    @Autowired
    private UserRepository UserDBRepo;

    @Autowired
    private TransactionRepository TransactionDBRepo;

    @Autowired
    private TransactionCache TransactionCacheReddis;

    @Autowired
    private AlertsRepository AlertsDBRepo;

    @Autowired
    private VaildationService vaildation;

    @Autowired
    private RedisUserCacheService CahceTransaction;

    @Autowired
    private DataSanitizationService DataSanatize;

    public Boolean AnaylizeTransaction(Transaction transaction) {
        boolean fraudulent = false;
        boolean inRange = true;
        boolean inHighRiskArea = false;

        String TransactionLocation = transaction.getLocation();
        String TransactionCity = TransactionLocation.split(",")[1];
        String TransactionProvince = ProvinceShortFormConversion(TransactionLocation.split(",")[2].trim());

        Alerts alerts = new Alerts();
        String UserLocation = "";

        UserCacheData CachedInfo = CahceTransaction.getUserCache(transaction.getUserId().toString());
        String VaildCache = vaildation.vaildatingUserCache(CachedInfo);
        logger.info("CacheData Vaildation results: " + VaildCache);

        if (!VaildCache.equalsIgnoreCase("GOOD!")) {
            CachedInfo = DataSanatize.SanatizeData(CachedInfo, VaildCache, transaction);
        }

        logger.info("AnaylizeTransaction - CachedInfo: " + CachedInfo);
        UserLocation = CachedInfo.getAddress();
        String UserLocationFormated = UserLocation.split(",")[1] + ", " + ProvinceShortFormConversion(UserLocation.split(",")[2]);
        String TransactionLocationFormated = TransactionCity + ", " + TransactionProvince;

        logger.info("AnaylizeTransaction - In high risk : " + HighRiskLocations.contains(TransactionLocationFormated.trim()));
        if (HighRiskLocations.contains(TransactionLocationFormated) && !TransactionLocationFormated.equals(UserLocationFormated)) {
            inHighRiskArea = true;
            alerts.setAlert_reason("Transaction within high risk area");
        }

        // CACHE DATA INTO REDDIS
        List<Transaction> UserTransactions = new ArrayList<>(CahceTransaction.getUserCache(transaction.getUserId().toString()).getTransactions());
        logger.info("AnaylizeTransaction - UserTransactions from Cache size: " + UserTransactions.size());

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
            CahceTransaction.updateUserTransactions(transaction.getUserId().toString(), UserTransactions);
        }

        logger.info("Transactions being used - All Transactions: ");
        for (Transaction tx : UserTransactions) {
            logger.info("Transaction ID: " + tx.getTransactionId()
                    + ", Amount: " + tx.getAmount()
                    + ", Date: " + tx.getTransaction_date()
                    + ", Location: " + tx.getLocation()
                    + ", Status: " + tx.getStatus());
        }

        double[] bounds = calculateBounds(UserTransactions, LocalDate.now(), 48.0);

        logger.info("AnaylizeTransaction - Lower Bound: " + bounds[0]);
        logger.info("AnaylizeTransaction - Upper Bound: " + bounds[1]);

        if (UserTransactions.size() < 2) {
            logger.info("AnaylizeTransaction - Not enough transactions to make a prediction, using 0 as bounds");
            bounds[0] = 0;
            bounds[1] = 200;
        }

        
        
        // Only flag as out-of-range if amount > upperBound OR (amount < lowerBound AND amount > 150)
        if (transaction.getAmount() > bounds[1] || (transaction.getAmount() < bounds[0] && transaction.getAmount() > 150)) {
            inRange = false;
            alerts.setAlert_reason("Out of predicted transaction amount");
        }
        
        

        fraudulent = (!inRange || inHighRiskArea);
        logger.info("AnaylizeTransaction - inRange: " + inRange);
        logger.info("AnaylizeTransaction - inHighRiskArea: " + inHighRiskArea);
        logger.info("AnaylizeTransaction - fraudulent: " + fraudulent);

        if (fraudulent) {
            logger.info("AnaylizeTransaction - alertData: " + alerts);
            alerts.setTransaction_id(transaction.getTransactionId());
            AlertsDBRepo.save(alerts);
            transaction.setStatus("DECLINED");
            // Send the message to RabbitMQ to alert the user that their transaction was declined
        } else {
            transaction.setStatus("APPROVED");
            UserTransactions.add(transaction);
        }

        // Add the transaction to the cache and the DB
        CahceTransaction.addTransaction(transaction.getUserId().toString(), transaction);
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

        if (transactions.size() < 2) return new double[]{0, 0}; // Need at least 2 points

        double lowerBound = 0;
        double upperBound = 100;
        double avgTransactionAmount = 0;

        LocalDate baseDate = transactions.get(0).getTransaction_date().toLocalDate();
        SimpleRegression regression = new SimpleRegression();

        long daysBetweenLastTransaction = ChronoUnit.DAYS.between(targetDate, baseDate);

        for (Transaction tx : transactions) {
            logger.info(" doing math - Transaction ID: " + tx.getTransactionId() + ", Amount: " + tx.getAmount() + ", Date: " + tx.getTransaction_date() + ", Location: " + tx.getLocation() + ", Status: " + tx.getStatus());
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

        if (daysBetweenLastTransaction > 30 || lowerBound < 0) {
            lowerBound = 0;
        }

        if (daysBetweenLastTransaction > 30 || upperBound < 0) {
            upperBound = avgTransactionAmount + SampleStandardDev(transactions, avgTransactionAmount);
        }

        return new double[]{lowerBound, upperBound};
    }

    private String ProvinceShortFormConversion(String province) {
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

    private static Double SampleStandardDev(List<Transaction> transactions, double mean) {
        Double SD = 0.0;
        for (Transaction tx : transactions) {
            SD += Math.pow((tx.getAmount() - mean), 2);
        }
        SD = Math.sqrt(SD / transactions.size());
        return SD;
    }
}
