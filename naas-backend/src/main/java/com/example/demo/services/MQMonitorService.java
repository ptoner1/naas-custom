package com.example.demo.services;

import com.ibm.mq.jakarta.jms.MQQueue;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


// NMW: This service is not functional.
@Service
public class MQMonitorService {

    @Autowired
    private ConnectionFactory connectionFactory;

    public int getQueueDepth(String queueName) {
        // Using JMSContext (Jakarta Messaging 3.1) for resource management
        try (JMSContext context = connectionFactory.createContext()) {
            MQQueue queue = (MQQueue) context.createQueue(queueName);
            
            // In Jakarta-compliant MQ libraries, we use the MQ-specific 
            // integer property via the CMQC constant for Current Depth.
            return queue.getIntProperty("CMQC.MQIA_CURRENT_Q_DEPTH");
        } catch (Exception e) {
            return -1;
        }
    }
}