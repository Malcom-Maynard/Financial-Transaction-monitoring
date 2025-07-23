package com.example.springbootbackend.Service.Reddis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.springbootbackend.model.Transaction;

import com.example.springbootbackend.config.RedisConfig;
@Service
public class RedisTransactionArrayService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveList(String id, List<Transaction> values) {
        redisTemplate.opsForValue().set(id, values);
    }

    public List<Transaction> getList(String id) {
        Object result = redisTemplate.opsForValue().get(id);
        if (result instanceof List) {
            return (List<Transaction>) result;
        }
        return Collections.emptyList();
    }

    public void deleteList(String id) {
        redisTemplate.delete(id);
    }


     
}
