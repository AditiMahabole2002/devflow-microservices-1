package com.devflow.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		System.out.println("------> JVM Timezone: " + java.util.TimeZone.getDefault());
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
