package com.tradingbot.dotty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO add Logs to specific files
// TODO Application restarts - webSockets

@SpringBootApplication()
public class DottyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DottyApplication.class, args);
	}

}
