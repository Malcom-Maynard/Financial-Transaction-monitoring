package com.example.springbootbackend.Service.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.springbootbackend.model.Transaction;

@Component
public class MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        logger.info("Infomation from RabbitMQ Server: "+message);


    }
}
