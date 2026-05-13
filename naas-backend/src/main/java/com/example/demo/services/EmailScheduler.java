package com.example.demo.services;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.models.Notification;

@Service
public class EmailScheduler {
    
    @Autowired
    private NotificationRepository repository;

    // @Autowired
    // private JavaMailSender mailSender;

    // Replaces JavaMailSender
    @Autowired
    private MQSenderService mqSender;

    // Runs every 10 seconds
    @Scheduled(fixedRate = 10000)
    public void processNotifications() {
        // 1. Fetch ready notifications as Entities (Standard practice for DB work)
        List<Notification> readyToSend = repository.findByIsDraftAndSendDateBefore("n", Instant.now());
    
        for (Notification notification : readyToSend) {
            try {
                // 2. Resolve the group publicIds to a DISTINCT list of email addresses
                List<String> uniqueEmails = repository.findUniqueEmailsByGroupPublicIds(notification.getRecipients());
    
                if (!uniqueEmails.isEmpty()) {
                    // 3. Hand off the work to the MQSender
                    // We pass the publicId so the MQ Listener knows which DB record to flag if it fails
                    mqSender.sendToQueue(uniqueEmails, notification.getSubject(), notification.getBody(), notification.getPublicId(), "n");
                }
    
                // 4. Mark the Entity as 's' (Sent/Queued) and save
                notification.setIsDraft("s"); 
                repository.save(notification);
                System.out.println("[SCHEDULER] Queued notification: " + notification.getPublicId());
                
            } catch (Exception e) {
                System.err.println("Failed to process Notification: " + notification.getPublicId() + " - " + e.getMessage());
            }
        }
    }


    // ***For use with JMS Sender - NOT MQ
    // private void sendEmail(Notification notification, List<String> recipients) {
    //     SimpleMailMessage message = new SimpleMailMessage();
    //     message.setFrom(notification.getSenderEmail());
    //     message.setSubject(notification.getSubject());
    //     message.setText(notification.getBody());

    //     message.setTo(recipients.toArray(new String[0]));

    //     mailSender.send(message);
    // }
}
