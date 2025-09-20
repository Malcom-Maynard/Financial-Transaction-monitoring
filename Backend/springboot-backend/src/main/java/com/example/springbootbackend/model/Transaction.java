package com.example.springbootbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

//Java Class representing a financial transaction made by a user in the system

@Entity
@Schema(description = "Financial Transaction Entity object representing a transaction made by a user")
public class Transaction {


    //Constructors
    public Transaction() {
    }

    public Transaction(UUID userId, Double amount, LocalDateTime transaction_date, String location, String status) {
        this.userId = userId;
        this.amount = amount;
        this.transaction_date = transaction_date;
        this.location = location;
        this.status = status;
    }



    @Id
    @Column(name = "transaction_id", updatable = false, nullable = false)
    @JsonProperty("transaction_id") // match JSON key
    @JsonAlias("transactionId")
    @Schema(description = "Unique identifier for the transaction", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID transactionId = UUID.randomUUID();

    @Schema(description = "Unique identifier for the user who made the transaction", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "Amount of money involved in the transaction", example = "250.75")
    private Double amount;

    @CreationTimestamp
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "transaction_date")
    @Schema(description = "Date and time when the transaction occurred", example = "2023-10-05T14:48:00")
    private LocalDateTime transaction_date;

    @Schema(description = "Location where the transaction took place", example = "New York, USA")
    private String location;

    @Schema(description = "Current status of the transaction", example = "PENDING")
    private String status;

    @PrePersist
    @PreUpdate
    // Ensure status is set to "PENDING" if not provided
    private void setDefaultStatus() {
        if (status == null || status.trim().isEmpty()) {
            status = "PENDING";
        }
    }

    //Getters and Setters
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


    //The Transaction date is set to the current date and time if not provided, since it is annotated with @CreationTimestamp.
    public LocalDateTime getTransaction_date() {
        if (transaction_date == null) {
            return LocalDateTime.now();
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
