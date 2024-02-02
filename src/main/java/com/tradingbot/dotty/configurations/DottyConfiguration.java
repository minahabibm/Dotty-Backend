package com.tradingbot.dotty.configurations;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableScheduling
public class DottyConfiguration {

//    @Value("${addressservice.base.url}")
    private String addressBaseUrl;


    @Bean
    public ModelMapper modelMapperBean() {
        return new ModelMapper();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(addressBaseUrl).build();
    }
}
