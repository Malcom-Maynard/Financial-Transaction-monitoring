package com.example.springbootbackend.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserCacheData implements Serializable {
    
    private List<Transaction> transactions;
    private String role;
    private String userName;
    private String userId;
    private String address;
    private String email;
    private String phone_number;
    private String name;

    private String LastUpdated;


    // Constructors
    public UserCacheData() {}

    

    public UserCacheData(List<Transaction> transactions, String address, String email, String LastUpdated, String role, String userName, String userId, String phone_number, String name) {
        this.transactions = transactions;
        this.address = address;
        this.email = email;
        this.LastUpdated = LastUpdated;
        this.role = role;
        this.userName = userName;   
        this.userId = userId; 
        this.phone_number = phone_number;
        this.name = name;  

    }


     public UserCacheData(UserCacheData data) {

        if(data!=null){
            this.transactions = data.getTransactions();
            this.address = data.getAddress();
            this.email = data.getEmail();
            this.LastUpdated = data.getLastUpdated();
            this.role = data.getRole(); 
            this.userName = data.getUserName();
            this.userId = data.getUserId();
            this.phone_number = data.getPhone_number();
            this.name = data.getName();

        }

        else{
            this.transactions = new ArrayList<Transaction>();
            this.address = "";
            this.LastUpdated = "";
            this.role = "";
            this.userName = "";
            this.email = "";
            this.userId = "";
            this.phone_number = "";
            this.name = "";
            

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone_number() {
        return phone_number;
    }
    public String getName() {
        return name;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
    public void setName(String name) {
        this.name = name;
    }



    
}