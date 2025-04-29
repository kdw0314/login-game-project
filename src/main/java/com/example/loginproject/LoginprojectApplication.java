package com.example.loginproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // ⭐ 이거 추가
public class LoginprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginprojectApplication.class, args);
	}

}
