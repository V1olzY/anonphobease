package com.vizako.anonphobease;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
public class AnonPhobEaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnonPhobEaseApplication.class, args);
	}

}
