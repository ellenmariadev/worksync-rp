package com.example.worksync;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorksyncApplicationTests {

    @Test
    void contextLoads() {
        ConfigurableApplicationContext context = SpringApplication.run(WorksyncApplication.class);
        assertNotNull(context);
        assertTrue(context.isRunning());
        context.close();
    }

    @Test
    void contextLoadsWithBuilder() {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(WorksyncApplication.class).run();
        assertNotNull(context);
        assertTrue(context.isRunning());
        context.close();
    }
}