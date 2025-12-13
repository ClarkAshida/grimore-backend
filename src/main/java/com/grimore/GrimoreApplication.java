package com.grimore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class GrimoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrimoreApplication.class, args);
	}

}
