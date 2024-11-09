package com.example.matcher.profileservice;

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
		SpringApplication.run(ProfileServiceApplication.class, args);
	}

}
