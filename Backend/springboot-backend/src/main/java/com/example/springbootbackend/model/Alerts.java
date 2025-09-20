package com.example.springbootbackend.model;



import com.example.springbootbackend.Service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import lombok.ToString;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;



//Java Class representing an alert generated for a transactions that have been flagged as potentially fraudulent or suspicious

@Entity
@Data
@ToString
public class Alerts {

    @Id   
    @GeneratedValue(generator = "UUID")
    @Column(name = "alert_id", updatable = false, nullable = false)
    @JsonProperty("id")  
    private UUID alertID;

    @JoinColumn(name = "transaction_id", foreignKey = @ForeignKey(name = "transaction_id"))
    @Column(name = "transaction_id")
    @JsonProperty("transaction_id") 
    private UUID transaction_id;


    @Column(name = "alert_reason")
    @JsonProperty("alert_reason")  
    private String alert_reason="";


    @CreationTimestamp
    @Column(name = "alert_date")
    @JsonProperty("alert_date")  
    private OffsetDateTime  alert_date;


    @Column(name = "resolved")
    @JsonProperty("resolved")
    private String resolved="FALSE";



   
      
}
