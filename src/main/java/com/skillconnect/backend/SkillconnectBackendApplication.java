package com.skillconnect.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SkillconnectBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillconnectBackendApplication.class, args);
	}

}
