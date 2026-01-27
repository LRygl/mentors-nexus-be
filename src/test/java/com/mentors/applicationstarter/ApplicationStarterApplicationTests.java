package com.mentors.applicationstarter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
		"spring.autoconfigure.exclude=" +
				"org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration," +
				"org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration"
})
class ApplicationStarterApplicationTests {

	@Test
	void contextLoads() {
	}

}
