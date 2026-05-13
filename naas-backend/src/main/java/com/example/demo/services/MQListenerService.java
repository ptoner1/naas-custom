package com.example.demo.services;

import com.example.demo.repository.NotificationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MQListenerService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.api.fromAddress}")
    private String fromAddress;

    @Autowired
    NotificationRepository notificationRepository;

    /**
     * This method "camps out" on the queue. 
     * As soon as a message arrives in DEV.QUEUE.1, it fires!
     */
    @Transactional
    @JmsListener(destination = "DEV.QUEUE.1")
    public void receiveMessage(String json, @Header("JMSXDeliveryCount") int backoutCount) {
        try {
            Map<String, String> emailData = objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
            try {
                // Bulk email Mock Test
                if (emailData.get("isMock").equals("y")) {
                    // Simulating 100ms of work per email.
                    Thread.sleep(100);
                    System.out.println("[MOCK] MQ Consumer grabbed message. Skipping SES call.");
                    return;
                }
                
                // Failure Mock Test
                if (json.contains("FORCE_FAIL")) {
                    System.out.println("[DLQ TEST] Poison message detected. Simulating technical failure...");
                    System.out.println("Current BackoutCount: " + backoutCount);
                    if (backoutCount > 2) { 
                        System.out.println("Final attempt failed. Flagging DB record as 'f' for DLQ audit.");
                        notificationRepository.updateStatusByPublicId(emailData.get("publicId"), "f");
                    }
                    throw new RuntimeException("Simulated Technical Failure for DLQ Demo");
                }
                
                String to = emailData.get("to");
                String subject = emailData.get("subject");
                String body = emailData.get("body");
                
                System.out.println("MQ Listener: Processing email for " + to);
                
                // Perform the actual AWS SES send logic
                sendActualEmail(to, subject, body);
                
            } catch (Exception e) {
                // If this fails, the message can be returned to the queue to try again
                System.err.println("MQ Listener Error: " + e.getMessage());
                System.out.println("Current BackoutCount: " + backoutCount);
                // If it fails 3 times, it goes to DLQ.  We must update the DB to show failure.
                if (backoutCount > 2) { 
                    System.out.println("Final attempt failed. Flagging DB record as 'f' for DLQ audit.");
                    notificationRepository.updateStatusByPublicId(emailData.get("publicId"), "f");
                }
                // Throwing exception is necessary to trigger the rollback
                throw new RuntimeException("Retry trigger: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void sendActualEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress); 
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        System.out.println("Email successfully delivered via AWS SES to: " + to);
    }
}