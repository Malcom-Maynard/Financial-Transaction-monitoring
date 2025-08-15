package com.example.springbootbackend.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserCacheData implements Serializable {
    
    private List<Transaction> transactions;
    private String address;
    private String email;
    private String LastUpdated;
    // Add other fields as needed

    // Constructors
    public UserCacheData() {}

    

    public UserCacheData(List<Transaction> transactions, String address, String email, String LastUpdated) {
        this.transactions = transactions;
        this.address = address;
        this.email = email;
        this.LastUpdated = LastUpdated;
    }


     public UserCacheData(UserCacheData data) {

        if(data!=null){
            this.transactions = data.getTransactions();
            this.address = data.getAddress();
            this.email = data.getEmail();

        }

        else{
            this.transactions = new ArrayList<Transaction>();
            this.address = "";
            this.email = "";

        }
        
    }

    
    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


     public String getLastUpdated() {
        return LastUpdated;
    }

    public void setLastUpdated(String LastUpdated) {
        this.LastUpdated = LastUpdated;
    }


    
}