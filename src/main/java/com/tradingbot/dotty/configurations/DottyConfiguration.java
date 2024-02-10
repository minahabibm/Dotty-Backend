package com.tradingbot.dotty.configurations;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class DottyConfiguration {

    @Bean
    public ModelMapper modelMapperBean() {
        return new ModelMapper();
    }


//    @Bean
//    public WebClient webClient() {
//        return WebClient.builder().baseUrl(baseUrl).build();
//    }
}
