package com.example.demo.dto;

public class AuthDTO {
    private String token;
    private String status;

    public AuthDTO(String token, String status) {
        this.token = token;
        this.status = status;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}