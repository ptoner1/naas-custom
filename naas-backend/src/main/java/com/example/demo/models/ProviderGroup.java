package com.example.demo.models;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "provider_groups")
public class ProviderGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private String publicId = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String description;

    @Column(name = "group_name", nullable = false)
    private String groupName;



    public ProviderGroup() {}

    public long getId() { return id; }
    public String getPublicId() { return publicId; }


    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }


    public String getGroupName() {return groupName; }
    public void setGroupName(String groupName) {this.groupName = groupName; }
}
