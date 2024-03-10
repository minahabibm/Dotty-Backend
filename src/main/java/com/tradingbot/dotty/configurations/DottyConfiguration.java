package com.tradingbot.dotty.configurations;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableScheduling
@EnableAsync
public class DottyConfiguration {

    @Bean
    public ModelMapper modelMapperBean() {
        return new ModelMapper();
    }

    @Primary
    @Bean(name = "taskExecutorDefault")
    public ThreadPoolTaskExecutor taskExecutorDefault() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Async-1-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "taskExecutorForHeavyTasks")
    public ThreadPoolTaskExecutor taskExecutorRegistration() {
        int poolSize = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize * 2);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("Async-2-");
        executor.setAllowCoreThreadTimeOut(true);
        executor.initialize();
        return executor;
    }


//    @Bean
//    public WebClient webClient() {
//        return WebClient.builder().baseUrl(baseUrl).build();
//    }
}
