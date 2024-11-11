package com.example.matcher.profileservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
//@EnableScheduling
//@EnableDiscoveryClient
public class ProfileServiceApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load(); // Загружаем переменные из .env
		System.setProperty("spring.datasource.url", dotenv.get("SPRING_DATASOURCE_URL"));
		System.setProperty("spring.datasource.username", dotenv.get("SPRING_DATASOURCE_USERNAME"));
		System.setProperty("spring.datasource.password", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
		System.setProperty("token.signing.key", dotenv.get("TOKEN_SIGNING_KEY"));
		System.setProperty("aws.access.key", dotenv.get("AWS_ACCESS_KEY"));
		System.setProperty("aws.secret.key", dotenv.get("AWS_SECRET_KEY"));
		SpringApplication.run(ProfileServiceApplication.class, args);
	}

}
