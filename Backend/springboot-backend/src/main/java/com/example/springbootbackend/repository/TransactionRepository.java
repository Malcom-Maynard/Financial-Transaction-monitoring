package com.example.springbootbackend.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springbootbackend.model.Transaction;

//Repository interface for Transaction entity for Database Logging and operations
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query(value = """
               Select *
            FROM (
                SELECT *
                FROM Transaction
                WHERE user_id = :userID
                and status != 'DECLINED'
                ORDER BY transaction_date DESC
                LIMIT :N
            ) sub
                """, nativeQuery = true)
    List<Transaction> UserLastNTransactions(@Param("userID") UUID userID, @Param("N") Integer N);
    // Fetch last N transactions for a user excluding DECLINED ones for the
    // creatition of lower and upper thresholds for fraud detection
}
