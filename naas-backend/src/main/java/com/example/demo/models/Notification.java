package com.example.demo.models;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private String publicId = UUID.randomUUID().toString();

    @Column(name = "sender_email")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String senderEmail;

    @Column(nullable = false)
    private String[] recipients;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String body;

    @Column(name = "is_draft", nullable = false)
    private String isDraft;

    @Column(name = "created_date")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "send_date")
    private Instant sendDate;


    

    public Notification(){ }

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getPublicId() { return publicId; }
    public void setPublicId(String publicId) { this.publicId = publicId; }

    public String[] getRecipients() { return recipients; }
    public void setRecipients(String[] recipients) { this.recipients = recipients; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getIsDraft() { return isDraft; }
    public void setIsDraft(String isDraft) { this.isDraft = isDraft; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime date) { this.createdDate = date; }

    public Instant getSendDate() { return sendDate; }
    public void setSendDate(Instant sendDate) { this.sendDate = sendDate; }
    
}
