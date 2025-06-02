package com.codebloom.cineman;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CinemanApplication {
	@Value("${jwt.secretKey}")
	private String secretKey;

	public static void main(String[] args) {
		SpringApplication.run(CinemanApplication.class, args);
	}

	@PostConstruct
	public void test() {
		System.out.println(secretKey);
	}

}
