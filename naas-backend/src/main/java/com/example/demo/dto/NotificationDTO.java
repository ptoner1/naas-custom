package com.example.demo.dto;

import java.time.Instant;

/**
 * DTO for transferring notification data between Angular and Spring Boot.
 * It uses a Record to ensure the data is immutable once received.
 */
public record NotificationDTO(
    String publicId,        // The UUID string for external identification
    String senderEmail,     // from address
    String subject,
    String body,
    String isDraft,         // 'y', 'n', 's', or 'f'
    String[] recipients,    // The array of provider group publicIds
    Instant sendDate,       // The ISO-8601 UTC timestamp
    String isMock           
) {}