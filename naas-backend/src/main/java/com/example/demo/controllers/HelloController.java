package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * A Record is a concise way to create a data transfer object (DTO).
 * Spring Boot will automatically serialize this to JSON.
 */
record Greeting(String message, String version) {}

@RestController
public class HelloController {

    /**
     * Handles GET requests to /hello
     * Usage: http://localhost:8080/hello?name=User
     */
    @GetMapping("/hello")
    public Greeting sayHello(@RequestParam(value = "name", defaultValue = "World") String name) {
        // Using Java 21's formatted strings and the Greeting record
        return new Greeting(
            "Hello, %s!".formatted(name), 
            "Java " + System.getProperty("java.version")
        );
    }
}

