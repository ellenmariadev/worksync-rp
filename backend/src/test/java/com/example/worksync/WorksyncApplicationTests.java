package com.example.worksync;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class WorksyncApplicationTests {

    @Test
    void contextLoads() {
        assertThat(SpringApplication.run(WorksyncApplication.class)).isNotNull();
    }

    @Test
    void mainMethodShouldStartApplication() {
        String[] args = {};
        try (ConfigurableApplicationContext context = SpringApplication.run(WorksyncApplication.class, args)) {
            assertThat(context).isNotNull();
        }
    }
}
