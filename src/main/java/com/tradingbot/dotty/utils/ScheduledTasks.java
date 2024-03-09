package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.dto.ScreenedTickersResponse;
import com.tradingbot.dotty.service.ScreenedTickersService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


// TODO Exceptions
// TODO Application restart
@Slf4j(topic = "Dotty_Scheduler")
@Service
public class ScheduledTasks {

    @Autowired
    private Utils utils;

    @Scheduled(cron = "0 58 19 * * MON,TUE,WED,THU,FRI,SAT,SUN")
    public void stockScreener() {
        log.info("Scheduled Stock Screening at {}", LocalDateTime.now());
        ScreenedTickersResponse[] ScreenedTickers = utils.stockScreenerUpdateRequest();
        if (ScreenedTickers != null)
            utils.selectAndSaveScreenedTickers();
    }

    @Scheduled(cron = "0 16 1 * * MON,TUE,WED,THU,FRI,SAT,SUN")
    public void tickerTechnicalAnalysis() {
        log.info("Scheduled ticker Technical Analysis polling start at {}", LocalDateTime.now());
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> utils.tickersTechnicalAnalysis();
        ScheduledFuture<?> schedulerHandle = executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
        Runnable canceller = () -> {
            log.info("Halt executing " + LocalDateTime.now());
            schedulerHandle.cancel(false);
            executor.shutdown(); // <---- Now the call is within the `canceller` Runnable.
        };
//        long seconds = ChronoUnit.SECONDS.between(LocalTime.now(),LocalTime.of(2,0));
        executor.schedule(canceller, 10, TimeUnit.SECONDS);
    }

//    @Scheduled(fixedRate = 1000000)
//    public void scheduledTasks() {
//        utils.subscribeToTickersTradesUpdate();
//    }

}
