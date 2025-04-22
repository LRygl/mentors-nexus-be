package com.mentors.applicationstarter;

import com.mentors.applicationstarter.Configuration.ApplicationConfig;
import com.mentors.applicationstarter.Service.AuthenticationService;
import com.mentors.applicationstarter.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;

import static com.mentors.applicationstarter.Constant.FileConstant.USER_FOLDER;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.mentors.applicationstarter.Repository")
@EnableAsync
public class ApplicationStarterApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStarterApplication.class);

	@Autowired
	private ApplicationConfig applicationConfig;

	public static void main(String[] args) {
		SpringApplication.run(ApplicationStarterApplication.class, args);
		File userFolder = new File(USER_FOLDER);
		if (!userFolder.mkdirs() && !userFolder.exists()) {
			throw new RuntimeException("Failed to create directory: " + USER_FOLDER);
		}
	}

	@Bean
	CommandLineRunner run(UserService userService, AuthenticationService authenticationService) {
		return args -> {
			if(applicationConfig.isRegisterAdminUserOnStartup()) {
				LOGGER.info("Admin user generation is enabled property isGenerateAdminUserOnApplicationStartup = " + applicationConfig.isRegisterAdminUserOnStartup() );
				if (userService.getUserById(1L).isEmpty()) {
					LOGGER.info("User with ID = 1 does not exist in the database - creating new ADMIN user with default application credentials");
					authenticationService.createAdminUser();
				} else {
					LOGGER.info("User with ID = 1 exists in the database - skipping creation of new ADMIN user");
				}
			} else {
				LOGGER.info("Admin user generation is disabled");
			}

		};
	}
}
