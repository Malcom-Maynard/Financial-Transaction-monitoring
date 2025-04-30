package com.example.springbootbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

import com.example.springbootbackend.Service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transaction {

    /*
     * Steps for processing
     * 1. Make API methods
     *  - Add transaction
     *  - Get traction (from transaction ID)
     *  - U 
     * 
     * 
     * 
     * 
     * 
     *  
        VaildPhoneNumber(user.getPhoneNumber());
        VaildEmail(user.getEmail());
        VaildName(user.getName());
        VaildPassword(user.getPassword());
        VaildRole(user.getRole());
        VaildUsername(user.getUsername());
     * 
     */


      @Id
      @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "txn_seq")
      @SequenceGenerator(name = "txn_seq", sequenceName = "transaction_sequence", allocationSize = 1)
      @Column(name = "Transaction_id", updatable = false, nullable = false)
      @JsonProperty("Transaction_id")  // Rename "userId" to "id" in the JSON output
      private Long TransactionID;


      private UUID userId;

      private Double amount;

      private Timestamp transaction_date;

      private String location;

      private String status;


      public UUID getUserId() {
         return userId;
     }
 
     public void setUserId(UUID userId) {
         this.userId = userId;
     }
 
     public Double getAmount() {
         return amount;
     }
 
     public void setAmount(Double amount) {
         this.amount = amount;
     }
 
     public Timestamp getTransaction_date() {
         return transaction_date;
     }
 
     public void setTransaction_date(Timestamp transaction_date) {
         this.transaction_date = transaction_date;
     }
 
     public String getLocation() {
         return location;
     }
 
     public void setLocation(String location) {
         this.location = location;
     }
 
     public String getStatus() {
         return status;
     }
 
     public void setStatus(String status) {
         this.status = status;
     }
 }






