package com.example.springbootbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;

import com.example.springbootbackend.Service.UserService;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.DefaultValue;


@Entity
public class Transaction {

   


    public Transaction() {
    }

    public Transaction( UUID userId, Double amount, LocalDateTime transaction_date, String location, String status) {
        this.userId = userId;
        this.amount = amount;
        this.transaction_date = transaction_date;
        this.location = location;
        this.status = status;
    }

    @Id
    @Column(name = "transaction_id", updatable = false, nullable = false)
    @JsonProperty("transaction_id")  // match JSON key
    @JsonAlias("transactionId")  
    private UUID transactionId = UUID.randomUUID();

    

      

      private UUID userId;

      private Double amount;
    
        @CreationTimestamp
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @Column(name = "transaction_date")
        private LocalDateTime transaction_date;

      


      private String location;


      
      private String status;
      
    

      @PrePersist
    @PreUpdate
    private void setDefaultStatus() {
        if (status == null || status.trim().isEmpty()) {
            status = "PENDING";
        }
    }

      public UUID getUserId() {
         return userId;
     }

     public UUID getTransactionId() {
         return transactionId;
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
 
     public LocalDateTime getTransaction_date() {
        if(transaction_date == null){
            return  LocalDateTime.now();
        }
         return transaction_date;
     }
 
     public void setTransaction_date(LocalDateTime transaction_date) {
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






