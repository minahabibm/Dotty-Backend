package com.tradingbot.dotty.utils;

import static com.tradingbot.dotty.utils.LoggingConstants.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class ScheduledTasks {

    @Autowired
    private Utils utils;

    @Scheduled(cron = Constants.STOCK_SCREENER_SCHEDULE)
    public void stockScreener() {
        log.info(SCHEDULED_TASK_START, "Stock Screening", LocalDateTime.now());
        utils.stockScreenerUpdate();
    }

    @Scheduled(cron = Constants.TECHNICAL_ANALYSIS_SCHEDULE)
    public void tickerTechnicalAnalysis() {
        log.info(SCHEDULED_TASK_START, "Ticker Technical Analysis Polling", LocalDateTime.now());
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> utils.tickersTechnicalAnalysis();
        ScheduledFuture<?> schedulerHandle = executor.scheduleAtFixedRate(task, 0, Constants.TA_API_POLLING_RATE, TimeUnit.SECONDS);
        Runnable canceller = () -> {
            log.info(SCHEDULED_TASK_END, LocalDateTime.now());
            schedulerHandle.cancel(false);
            executor.shutdown(); // <---- Now the call is within the `canceller` Runnable.
        };
        long seconds = ChronoUnit.SECONDS.between(LocalTime.now(), LocalTime.of(Constants.TA_API_STOP_POLLING_HOUR, Constants.TA_API_STOP_POLLING_MINUTE));
        executor.schedule(canceller, seconds, TimeUnit.SECONDS);
    }

    @Scheduled(fixedRate = 1000000)
    public void scheduledTasks() {

    }

}
