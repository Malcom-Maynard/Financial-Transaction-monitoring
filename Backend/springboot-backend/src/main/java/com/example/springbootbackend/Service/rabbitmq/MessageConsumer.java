package com.example.springbootbackend.Service.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.springbootbackend.Service.FraudDetectionService;
import com.example.springbootbackend.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;


//Message Consumer looks out for messages being sent to the Inbound Queue to be processed and checked for Fraud
@Component
public class MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

   

   @Autowired 
   private FraudDetectionService FraudDetection;
    

    @RabbitListener(queues = RabbitMQConfig.QUEUE_INBOUND)
    public void receiveMessageINBOUND(Transaction message) throws JsonProcessingException {

        logger.info("Information from Transaction-Q-IN: "+message.toString());


        Boolean Result = FraudDetection.AnalyzeTransaction(message);
        logger.info("MessageConsumer- Result value : "+Result);
        
    }



   
}
