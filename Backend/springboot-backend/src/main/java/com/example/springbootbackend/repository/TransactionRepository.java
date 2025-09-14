package com.example.springbootbackend.repository;




import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springbootbackend.model.Transaction;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    

     @Query(value = """
        SELECT AVG(sub.amount)
    FROM (
        SELECT amount
        FROM Transaction
        WHERE user_id = :userID
        ORDER BY amount ASC
        LIMIT 3
    ) sub
        """, nativeQuery = true)
    Double UserLowerBondAverage(@Param("userID") UUID userID);





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


    @Query(value = """
        SELECT AVG(sub.amount)
    FROM (
        SELECT amount
        FROM Transaction
        WHERE user_id = :userID
        ORDER BY amount DESC
        LIMIT 3
    ) sub
        """, nativeQuery = true)
    Double UserHigherBondAverage(@Param("userID") UUID userID);


    @Query(value = """
        Select AVG(amount) FROM Transaction  WHERE user_id = :userID
        """, nativeQuery = true)
    Double UserTransactionAverage(@Param("userID") UUID userID);


     @Query(value = """
        Select AVG(amount) FROM Transaction  WHERE location  LIKE CONCAT('%',:location,'%')
        """, nativeQuery = true)
    Double AverageTransactionAmountPerCity(@Param("location") String location);



     @Query(value = """
        Select AVG(amount) FROM Transaction
        
        """, nativeQuery = true)
    Double AverageTransactionAmountTotal();


    
}
