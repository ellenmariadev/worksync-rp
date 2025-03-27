package com.example.worksync;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") 
class WorksyncApplicationTests {

	@Test
	void contextLoads() {
		Assertions.assertThat(true).isTrue();
	}

}
