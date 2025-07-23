package com.example.springbootbackend.repository.Reddis;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springbootbackend.model.Alerts;
import com.example.springbootbackend.model.Transaction;

@Repository
public interface TransactionCache extends CrudRepository<Transaction, UUID>{


  



}
    

