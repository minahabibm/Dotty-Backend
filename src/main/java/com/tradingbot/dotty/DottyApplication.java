package com.tradingbot.dotty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO WebSockets
// TODO Messaging
// TODO Application restarts
// TODO Exceptions Handling \ Retries
// TODO user redirects to login page

@SpringBootApplication()
public class DottyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DottyApplication.class, args);
	}

}
