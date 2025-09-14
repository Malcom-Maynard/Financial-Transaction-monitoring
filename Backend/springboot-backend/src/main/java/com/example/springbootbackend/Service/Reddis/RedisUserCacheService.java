package com.example.springbootbackend.Service.Reddis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.springbootbackend.model.Transaction;
import com.example.springbootbackend.model.UserCacheData;
import com.example.springbootbackend.config.RedisConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Service
public class RedisUserCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveUserCache(String userId, UserCacheData userData) {
        redisTemplate.opsForValue().set(userId, userData);
    }

    public void saveUserCache(String userId, UserCacheData userData, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(userId, userData, timeout, unit);
    }

    public UserCacheData getUserCache(String userId) {
        Object result = redisTemplate.opsForValue().get(userId);
        if (result instanceof UserCacheData) {
            return (UserCacheData) result;
        }
        return null;
    }

    public void deleteUserCache(String userId) {
        redisTemplate.delete(userId);
    }

   
    public boolean updateUserAddress(String userId, String newAddress) {
        UserCacheData existing = getUserCache(userId);
        if (existing != null) {
            existing.setAddress(newAddress);
            existing.setLastUpdated(LocalDateTime.now().toString());
            saveUserCache(userId, existing);
            return true;
        }
        return false;
    }

    
    public boolean updateUserEmail(String userId, String newEmail) {
        UserCacheData existing = getUserCache(userId);
        if (existing != null) {
            existing.setEmail(newEmail);
            existing.setLastUpdated(LocalDateTime.now().toString());
            saveUserCache(userId, existing);
            return true;
        }
        return false;
    }

    
    public boolean updateUserTransactions(String userId, List<Transaction> newTransactions) {
        UserCacheData existing = getUserCache(userId);
        if (existing != null) {
            existing.setTransactions(newTransactions);
            saveUserCache(userId, existing);
            return true;
        }
        return false;
    }

    
    public boolean addTransaction(String userId, Transaction newTransaction) {
        UserCacheData existing = getUserCache(userId);
        if (existing != null) {
            List<Transaction> txList = existing.getTransactions();
            txList.add(newTransaction);
            existing.setTransactions(txList);
            saveUserCache(userId, existing);
            return true;
        }
        return false;
    }

    
    public boolean removeTransactionById(String userId, String transactionId) {
        UserCacheData existing = getUserCache(userId);
        if (existing != null && existing.getTransactions() != null) {
            boolean removed = existing.getTransactions().removeIf(tx -> tx.getUserId().toString().equals(transactionId));
            if (removed) {
                saveUserCache(userId, existing);
            }
            return removed;
        }
        return false;
    }

    public Transaction getTransactionById(String userId, String transactionId) {
    UserCacheData existing = getUserCache(userId);
    if (existing != null && existing.getTransactions() != null) {
        for (Transaction tx : existing.getTransactions()) {
            if (tx.getTransactionId().toString().equals(transactionId)) {
                return tx;
            }
        }
    }
    return null;
}

    
}