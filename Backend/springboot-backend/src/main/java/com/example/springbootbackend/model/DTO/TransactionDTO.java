package com.example.springbootbackend.model.DTO;

import com.example.springbootbackend.model.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_dto")
@Schema(description = "Transaction Data Transfer Object")
public class TransactionDTO {

    @Id
    @Schema(description = "Transaction ID")
    private UUID transactionId;

    @Schema(description = "User ID")
    private UUID userId;

    @Schema(description = "Transaction amount")
    private double amount;

    @Schema(description = "Transaction location")
    private String location;

    @Schema(description = "Transaction status")
    private String status;

    @Schema(description = "Transaction date/time")
    private OffsetDateTime transaction_date;

    // No-args constructor required by JPA
    public TransactionDTO() {}

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
