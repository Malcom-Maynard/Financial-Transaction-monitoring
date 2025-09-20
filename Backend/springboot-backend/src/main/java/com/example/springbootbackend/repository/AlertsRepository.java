package com.example.springbootbackend.repository;


import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.springbootbackend.model.Alerts;

//Repository interface for Alerts entity for Database Logging and operations
@Repository
public interface AlertsRepository extends JpaRepository<Alerts, UUID>{
    
}
