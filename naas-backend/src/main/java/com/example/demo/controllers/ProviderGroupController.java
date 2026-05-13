package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.ProviderGroup;
import com.example.demo.repository.ProviderGroupRepository;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/providers") // Class-level prefix
public class ProviderGroupController {

    private final ProviderGroupRepository repository;

    public ProviderGroupController(ProviderGroupRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    public ResponseEntity<List<ProviderGroup>> getProviders() {
        List<ProviderGroup> providers = this.repository.findAll();
        return ResponseEntity.ok(providers);
    }
    
}
