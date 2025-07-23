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
/*
 * RabbitMQ Messages to be sent
 * - To RabbitMQ exchange to be processed
 * 
 * 
 */


@Service
public class MessageSender {

    @Autowired
    private AmqpTemplate amqpTemplate;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /*
    Change the method to be able to select the correct Routing Key
        Routing Keys:
            1: Transaction-Q-IN
                O sends data to the processing Q that will be picked up by the backend

            2: Transaction-Q-Out
                O sends data to the controller layer( contains status of the processing in backend)

    */
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



