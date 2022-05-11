package ru.megboyzz.smartstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SmartstoreApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(SmartstoreApplication.class, args);
	}

}
