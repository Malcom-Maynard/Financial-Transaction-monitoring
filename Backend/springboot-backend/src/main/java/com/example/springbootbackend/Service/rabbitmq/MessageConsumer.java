package com.example.springbootbackend.Service.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.springbootbackend.Service.FraudDectionService;
import com.example.springbootbackend.Service.TransactionService;
import com.example.springbootbackend.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.example.springbootbackend.model.Transaction;
 /*
    Change the method to be able to select the correct Q
        Routing Keys:
            1: Transaction-Q-IN
                O 

            2: Transaction-Q-Out
                O The Q consume messages from the backend when the processing of data is done

    */
@Component
public class MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    @Autowired
    private MessageSender messageSender;

   @Autowired 
   private FraudDectionService FradDection;
    

    @RabbitListener(queues = RabbitMQConfig.QUEUE_INBOUND)
    public void receiveMessageINBOUND(Transaction message) throws JsonProcessingException {

        logger.info("Infomation from Transaction-Q-IN: "+message.toString());


        Boolean Result = FradDection.AnaylizeTransaction(message);
        logger.info("MessageConsumer- Result value : "+Result);
        
        


    }



   
}
