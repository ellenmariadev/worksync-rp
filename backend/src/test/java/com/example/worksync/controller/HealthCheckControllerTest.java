package com.example.worksync.controller;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class HealthCheckControllerTest {

    private final HealthCheckController healthCheckController = new HealthCheckController();

    @Test
    void welcome_shouldReturnWelcomeMessage() {
        Map<String, String> result = healthCheckController.welcome();
        assertEquals("Welcome to Worksync!", result.get("message"));
    }
}