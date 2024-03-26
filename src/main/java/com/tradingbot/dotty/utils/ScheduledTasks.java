package com.tradingbot.dotty.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;


@Slf4j
@Service
public class ScheduledTasks {

    @Autowired
    private Utils utils;

    @Scheduled(cron = Constants.STOCK_SCREENER_SCHEDULE)
    public void stockScreener() {
        log.info("Scheduled Stock Screening at {}", LocalDateTime.now());
        utils.stockScreenerUpdate();
    }

    @Scheduled(cron = Constants.TECHNICAL_ANALYSIS_SCHEDULE)
    public void tickerTechnicalAnalysis() {
        log.info("Scheduled ticker Technical Analysis polling start at {}", LocalDateTime.now());
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> utils.tickersTechnicalAnalysis();
        ScheduledFuture<?> schedulerHandle = executor.scheduleAtFixedRate(task, 0, 10000000, TimeUnit.SECONDS);
        Runnable canceller = () -> {
            log.info("Halt executing " + LocalDateTime.now());
            schedulerHandle.cancel(false);
            executor.shutdown(); // <---- Now the call is within the `canceller` Runnable.
        };
        long seconds = ChronoUnit.SECONDS.between(LocalTime.now(),LocalTime.of(23,36));
        executor.schedule(canceller, seconds, TimeUnit.SECONDS);
    }

    @Scheduled(fixedRate = 1000000)
    public void scheduledTasks() {
//        utils.stockScreenerUpdate();
//        utils.tickersTechnicalAnalysis();
//        utils.subscribeToTickersTradesUpdate("IWM");
    }

}
