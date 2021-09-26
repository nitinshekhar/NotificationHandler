package com.nitin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka	
public class NotificationHandlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationHandlerApplication.class, args);
	}

}
