package com.tradingbot.dotty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// TODO Application restarts

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class DottyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DottyApplication.class, args);
	}

}
