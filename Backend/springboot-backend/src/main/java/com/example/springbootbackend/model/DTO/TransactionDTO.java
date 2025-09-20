package com.example.springbootbackend.model.DTO;

import com.example.springbootbackend.model.Transaction;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Schema(hidden = true)
@Table(name = "transaction_dto")

public class TransactionDTO {

    @Id

    private UUID transactionId;

    private UUID userId;

    private double amount;

    private String location;

    private String status;

    private OffsetDateTime transaction_date;

    // Constructors
    public TransactionDTO() {
    }

    public TransactionDTO(Transaction transaction) {
        this.transactionId = transaction.getTransactionId();
        this.userId = transaction.getUserId();
        this.amount = transaction.getAmount();
        this.location = transaction.getLocation();
        this.status = transaction.getStatus();
        this.transaction_date = transaction.getTransaction_date().atOffset(java.time.ZoneOffset.UTC);
    }

    // Getters
    public UUID getTransactionId() {
        return transactionId;
    }

    public UUID getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getTransaction_date() {
        return transaction_date;
    }
}
