package com.example.demo.controllers;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AuthDTO;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
public class UserController { 

    @Value("${app.api.frontendAuth}")
    private String authCode;

    @Value("${app.api.jwtSecret}")
    private String jwtSecret; 


    @GetMapping("/{code}")
    public ResponseEntity<AuthDTO> authorizeUser(@PathVariable String code) {    
        if (code.equals(authCode)) {
            // Generate a token valid for 24 hours
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            String generatedToken = Jwts.builder()
                    .subject("authenticatedUser")
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                    .signWith(key)
                    .compact();

            return ResponseEntity.ok(new AuthDTO(generatedToken, "success"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthDTO(null, "failed"));
        }
    }
}
