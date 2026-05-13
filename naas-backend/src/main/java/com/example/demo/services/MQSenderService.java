package com.example.demo.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MQSenderService {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendToQueue(List<String> recipients, String subject, String body, String publicId, String isMock) {
        for (String email : recipients) {
            // Create a simple Map to act as the JSON payload
            Map<String, String> mqPayload = new HashMap<>();
            mqPayload.put("to", email); // Translate 'recipients' array element to 'to' string
            mqPayload.put("subject", subject);
            mqPayload.put("body", body);
            mqPayload.put("publicId", publicId); // Crucial for DLQ status updates
            mqPayload.put("isMock", isMock);
    
            try {
                String json = objectMapper.writeValueAsString(mqPayload);
                jmsTemplate.convertAndSend("DEV.QUEUE.1", json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize MQ payload", e);
            }
        }
    }

}