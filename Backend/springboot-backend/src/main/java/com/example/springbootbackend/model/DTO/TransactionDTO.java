package com.example.springbootbackend.model.DTO;

import com.example.springbootbackend.model.Transaction;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TransactionDTO {
    private UUID transactionId;
    private UUID userId;
    private double amount;
    private String location;
    private String status;
    private OffsetDateTime transaction_date;

    public TransactionDTO(Transaction transaction) {
        this.transactionId = transaction.getTransactionId();
        this.userId = transaction.getUserId();
        this.amount = transaction.getAmount();
        this.location = transaction.getLocation();
        this.status = transaction.getStatus();
        this.transaction_date = transaction.getTransaction_date().atOffset(java.time.ZoneOffset.UTC);
    }

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
