package com.tradingbot.dotty.utils;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;

import com.tradingbot.dotty.utils.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


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
        utils.technicalAnalysisPolling();
    }

//    int count = 0;
//    @Scheduled(fixedRate = 1000)
//    public void scheduledTasks() throws IOException {
//        System.out.println("hello");
//        webSocketService.broadcast("/topic/public", "Hello User!  Hello User!  Hello User!  Hello User!  Hello User!  Hello User!  Hello User! Hello User!  Hello User!  Hello User!  Hello User!  Hello User!  Hello User!  Hello User! Hello User!  Hello User!Hello User!  Hello User!  Hello User!  Hello User!  Hello User!"+ count++);
//        webSocketService.broadcast("/queue/private", "user_id", "hello  there!");

//        orderProducer.sendExitOrder("Exit order for stock XYZ");
//        orderProducer.sendEntryOrder("Entry order for stock ABC");

//    }

}
