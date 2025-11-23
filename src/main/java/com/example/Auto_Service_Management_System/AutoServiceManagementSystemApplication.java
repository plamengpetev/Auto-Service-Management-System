package com.example.Auto_Service_Management_System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.Auto_Service_Management_System.client")
public class AutoServiceManagementSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(AutoServiceManagementSystemApplication.class, args);
	}
}

