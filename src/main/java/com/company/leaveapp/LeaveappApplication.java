package com.company.leaveapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LeaveappApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeaveappApplication.class, args);
	}

}
