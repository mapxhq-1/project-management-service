package com.example.ProjectManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing

public class ProjectManagement{

	public static void main(String[] args) {
		SpringApplication.run(ProjectManagement.class, args);
	}

}
