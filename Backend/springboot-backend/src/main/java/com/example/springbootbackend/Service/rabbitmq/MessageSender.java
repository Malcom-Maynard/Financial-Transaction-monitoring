package com.example.springbootbackend.Service.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.example.springbootbackend.model.Transaction;


import com.example.springbootbackend.Service.UserService;


//Message Sender sends messages to the RabbitMQ Queues to be processed
@Service
public class MessageSender {

    @Autowired
    private AmqpTemplate amqpTemplate;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Async
    public void sendMessageIN(Transaction message) {

        
        amqpTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_IN, message);
        logger.info("Message sent to Queue-Transaction-Q-IN: "+message);

        
    }



    @Async
    public void sendMessageOUT(String message) {

        
        amqpTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_OUT, message);
        logger.info("Message sent to rabbitMQ: "+message);
    }



  

     
}



