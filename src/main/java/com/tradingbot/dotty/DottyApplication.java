package com.tradingbot.dotty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class DottyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DottyApplication.class, args);
	}

}
