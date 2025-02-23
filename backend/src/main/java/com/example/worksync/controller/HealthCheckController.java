package com.example.worksync.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class HealthCheckController {
    @GetMapping
    public Map<String, String> welcome() {
        Map<String, String> welcomeMessage = new HashMap<>();
        welcomeMessage.put("message", "Welcome to Worksync!");
        return welcomeMessage;
    }
}