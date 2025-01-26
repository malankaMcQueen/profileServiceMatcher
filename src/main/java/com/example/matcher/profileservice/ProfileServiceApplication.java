package com.example.matcher.profileservice;

import com.example.matcher.profileservice.tools.EnvironmentLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
//@EnableScheduling
@EnableDiscoveryClient
public class ProfileServiceApplication {
	public static void main(String[] args) {
		System.setProperty("spring.datasource.url", EnvironmentLoader.get("SPRING_DATASOURCE_URL"));
		System.setProperty("spring.datasource.username", EnvironmentLoader.get("SPRING_DATASOURCE_USERNAME"));
		System.setProperty("spring.datasource.password", EnvironmentLoader.get("SPRING_DATASOURCE_PASSWORD"));
		System.setProperty("token.signing.key", EnvironmentLoader.get("TOKEN_SIGNING_KEY"));
		System.setProperty("aws.access.key", EnvironmentLoader.get("AWS_ACCESS_KEY"));
		System.setProperty("aws.secret.key", EnvironmentLoader.get("AWS_SECRET_KEY"));
		System.setProperty("aws.s3.my_domain", EnvironmentLoader.get("AWS_MY_DOMAIN"));
		System.setProperty("aws.s3.buck_name", EnvironmentLoader.get("AWS_BUCK_NAME"));

		System.out.println(System.getProperties());
		SpringApplication.run(ProfileServiceApplication.class, args);
	}

}
