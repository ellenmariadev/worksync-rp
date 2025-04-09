package com.example.worksync;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

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
