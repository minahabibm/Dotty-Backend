package com.tradingbot.dotty.configurations;

import com.tradingbot.dotty.models.*;
import com.tradingbot.dotty.models.dto.*;
import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableScheduling
@EnableCaching
@EnableAsync
public class DottyConfiguration {

    @Bean
    public ModelMapper modelMapperBean() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(TickersTradeUpdates.class, TickersTradeUpdatesDTO.class).addMapping(TickersTradeUpdates::getScreenedTicker, TickersTradeUpdatesDTO::setScreenedTickerDTO);
        modelMapper.typeMap(TickersTradeUpdatesDTO.class, TickersTradeUpdates.class).addMapping(TickersTradeUpdatesDTO::getScreenedTickerDTO, TickersTradeUpdates::setScreenedTicker);
        modelMapper.typeMap(Position.class, PositionDTO.class).addMapping(Position::getPositionTracker, PositionDTO::setPositionTrackerDTO);
        modelMapper.typeMap(PositionDTO.class, Position.class).addMapping(PositionDTO::getPositionTrackerDTO, Position::setPositionTracker);
        modelMapper.typeMap(Orders.class, OrdersDTO.class).addMapping(Orders::getPositionTracker, OrdersDTO::setPositionTrackerDTO);
        modelMapper.typeMap(OrdersDTO.class, Orders.class).addMapping(OrdersDTO::getPositionTrackerDTO, Orders::setPositionTracker);
        modelMapper.typeMap(Users.class, UsersDTO.class).addMapping(Users::getUserConfiguration, UsersDTO::setUserConfigurationDTO);
        modelMapper.typeMap(UsersDTO.class, Users.class).addMapping(UsersDTO::getUserConfigurationDTO, Users::setUserConfiguration);
        modelMapper.typeMap(UserOrder.class, UserOrderDTO.class).addMapping(UserOrder::getUsers, UserOrderDTO::setUsersDTO);
        modelMapper.typeMap(UserOrderDTO.class, UserOrder.class).addMapping(UserOrderDTO::getUsersDTO, UserOrder::setUsers);
        modelMapper.typeMap(UserOrder.class, UserOrderDTO.class).addMapping(UserOrder::getOrders, UserOrderDTO::setOrdersDTO);
        modelMapper.typeMap(UserOrderDTO.class, UserOrder.class).addMapping(UserOrderDTO::getOrdersDTO, UserOrder::setOrders);
        modelMapper.typeMap(UserHolding.class, UserHoldingDTO.class).addMapping(UserHolding::getUsers, UserHoldingDTO::setUsersDTO);
        modelMapper.typeMap(UserHoldingDTO.class, UserHolding.class).addMapping(UserHoldingDTO::getUsersDTO, UserHolding::setUsers);
        modelMapper.typeMap(UserHolding.class, UserHoldingDTO.class).addMapping(UserHolding::getHolding, UserHoldingDTO::setHoldingDTO);
        modelMapper.typeMap(UserHoldingDTO.class, UserHolding.class).addMapping(UserHoldingDTO::getHoldingDTO, UserHolding::setHolding);
        return modelMapper;
    }

    @Primary
    @Bean(name = "taskExecutorDefault")
    public ThreadPoolTaskExecutor taskExecutorDefault() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(1000);
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

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("tokens");
    }

}
