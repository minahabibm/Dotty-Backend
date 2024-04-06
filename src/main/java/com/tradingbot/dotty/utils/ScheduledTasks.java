package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.service.ScreenedTickersService;
import com.tradingbot.dotty.service.TickerMarketTradeService;
import com.tradingbot.dotty.service.TickersTradeUpdatesService;
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

    @Autowired
    private ExternalApiRequests apiRequests;

    @Autowired
    private ConcurrentMarketDataFunnel marketDataFunnel;



    @Scheduled(cron = Constants.STOCK_SCREENER_SCHEDULE)
    public void stockScreener() {
        log.info("Scheduled Stock Screening at {}", LocalDateTime.now());
        utils.stockScreenerUpdate();
    }

    @Scheduled(cron = Constants.TECHNICAL_ANALYSIS_SCHEDULE)
    public void tickerTechnicalAnalysis() {
        log.info("Scheduled ticker Technical Analysis polling start at {}", LocalDateTime.now());
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> utils.tickersTechnicalAnalysis();
        ScheduledFuture<?> schedulerHandle = executor.scheduleAtFixedRate(task, 0, Constants.TA_API_POLLING_RATE, TimeUnit.SECONDS);
        Runnable canceller = () -> {
            log.info("Halt executing at" + LocalDateTime.now());
            schedulerHandle.cancel(false);
            executor.shutdown(); // <---- Now the call is within the `canceller` Runnable.
        };
        long seconds = ChronoUnit.SECONDS.between(LocalTime.now(), LocalTime.of(Constants.TA_API_STOP_POLLING_HOUR, Constants.TA_API_STOP_POLLING_MINUTE));
        executor.schedule(canceller, seconds, TimeUnit.SECONDS);
    }

    @Scheduled(fixedRate = 1000000)
    public void scheduledTasks() {
        utils.stockScreenerUpdate();
        utils.tickersTechnicalAnalysis();

    }

}
