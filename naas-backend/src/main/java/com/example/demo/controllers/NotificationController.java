package com.example.demo.controllers;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.repository.NotificationRepository;
import com.example.demo.services.MQMonitorService;
import com.example.demo.services.MQSenderService;
import com.example.demo.models.Notification;


@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository repository;

    // For Batch Testing
    @Autowired
    MQSenderService mqSenderService;

    @Autowired
    MQMonitorService mqMonitorService;

    @Value("${app.api.fromAddress}")
    private String fromAddress;

    // @Autowired
    public NotificationController(NotificationRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    public List<Notification> getNotifications() {
        List<Notification> notifications = this.repository.findAll();
        return notifications;
    }

    @PostMapping("")
    public ResponseEntity<Notification> saveNotification(@RequestBody Notification notification) {
        // Check if this is an edit of an existing notification
        if (notification.getPublicId() != null) {
            repository.findByPublicId(notification.getPublicId()).ifPresent(existing -> {
                // Attach the internal ID so Hibernate knows to UPDATE instead of INSERT
                notification.setId(existing.getId());
                // Preserve the original created date
                notification.setCreatedDate(existing.getCreatedDate());
            });
        }

        notification.setSenderEmail(fromAddress);
        if (notification.getSendDate() == null) {
            notification.setSendDate(Instant.now());
        }

        Notification savedNotification = repository.save(notification);

        HttpStatus status = (notification.getId() != null) ? HttpStatus.OK : HttpStatus.CREATED;
        return new ResponseEntity<>(savedNotification, status);
    }
    
    @GetMapping("/mq-status")
    public ResponseEntity<Map<String, Object>> getMQStatus() {
        int depth = mqMonitorService.getQueueDepth("DEV.QUEUE.1");

        Map<String, Object> status = new HashMap<>();
        status.put("online", depth >= 0);
        status.put("queueDepth", Math.max(depth, 0));
        status.put("brokerName", "QM1");

        return ResponseEntity.ok(status);
    }

    @PostMapping("/stress-test/{count}")
    public ResponseEntity<String> stressTest(@PathVariable int count) {
        if (count > 500) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Batch size too large for demo. Please use 500 or less.");
        }
        for (int i = 0; i < count; i++) {
            List<String> mockAddresses = new ArrayList<>();
            mockAddresses.add("MockTest");
            mqSenderService.sendToQueue(mockAddresses, "Test msg" + i, "Body", "1" + i, "y");
        }
        return ResponseEntity.ok("Flooding queue with " + count + " messages.");
    }

}