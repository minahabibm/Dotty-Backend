package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.TickersTradeUpdates;
import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;
import com.tradingbot.dotty.models.dto.requests.ScreenedTickersResponse;
import com.tradingbot.dotty.models.dto.requests.TechnicalIndicatorResponse;
import com.tradingbot.dotty.models.dto.TickersTradeUpdatesDTO;
import com.tradingbot.dotty.service.ScreenedTickersService;
import com.tradingbot.dotty.service.TickersTradeUpdatesService;
import com.tradingbot.dotty.utils.ExternalAPi.TickerUtil;
import com.tradingbot.dotty.utils.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;


@Slf4j
@Service
public class Utils {

    @Autowired
    private TickerUtil tickerUtil;

    @Autowired
    private ScreenedTickersService screenedTickersService;

    @Autowired
    private TickersTradeUpdatesService tickersTradeUpdatesService;

    @Autowired
    private ConcurrentMarketDataFunnel marketDataFunnel;

    @Autowired
    private ModelMapper modelMapper;

    public void stockScreenerUpdate() {
        log.debug(SCREENING_TICKERS);
        ScreenedTickersResponse[] screenedTickersResponse = tickerUtil.stockScreenerUpdateRetrieve();
        if (screenedTickersResponse != null) {
            log.debug(SCREENED_TICKERS_SAVING, screenedTickersResponse);
            screenedTickersService.insertAndUpdateScreenedTickers(Arrays.stream(screenedTickersResponse).map(screenedTicker -> modelMapper.map(screenedTicker, ScreenedTickerDTO.class)).collect(Collectors.toList()));

            selectAndSaveScreenedTickers();
        }
    }

    public void selectAndSaveScreenedTickers(){
        log.debug(SCREENED_TICKERS_PROCESSING);
        List<ScreenedTickerDTO> screenedTickerDTOS = screenedTickersService.getTodayScreenedTickers();

        log.debug(SCREENED_TICKERS_FILTERING);
        List<ScreenedTicker> screenedTickers = screenedTickerDTOS.stream()
                .filter(x -> (x.getSector()!=null && (x.getSector().contains("Consumer Cyclical") || x.getSector().contains("Technology") || x.getSector().contains("Communication Services"))))
                .map(x -> modelMapper.map(x, ScreenedTicker.class))
                .toList();

        log.debug(SCREENED_TICKERS_TO_MARKET_TRADE);
        List<TickersTradeUpdates> tickersTradeUpdates = screenedTickers.stream().map(x -> modelMapper.map(x,TickersTradeUpdates.class)).collect(Collectors.toList());
        tickersTradeUpdatesService.insertTickersTradeUpdates(tickersTradeUpdates);
    }

    public void technicalAnalysisPolling() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> tickersTechnicalAnalysis();
        ScheduledFuture<?> schedulerHandle = executor.scheduleAtFixedRate(task, 0, Constants.TA_API_POLLING_RATE, TimeUnit.SECONDS);
        Runnable canceller = () -> {
            log.info(SCHEDULED_TASK_END, LocalDateTime.now());
            schedulerHandle.cancel(false);
            executor.shutdown(); // <---- Now the call is within the `canceller` Runnable.
        };
        long seconds = ChronoUnit.SECONDS.between(LocalTime.now(), LocalTime.of(Constants.TA_API_STOP_POLLING_HOUR, Constants.TA_API_STOP_POLLING_MINUTE));
        executor.schedule(canceller, seconds, TimeUnit.SECONDS);
    }

    public void tickersTechnicalAnalysis() {
        log.debug(TICKER_TECHNICAL_ANALYSIS, LocalDateTime.now());

        // Get Ticker Trades Updates
        log.debug(TICKER_TECHNICAL_ANALYSIS_SORTED_TICKERS);
        List<TickersTradeUpdatesDTO> tickersTradeUpdates = tickersTradeUpdatesService.getSortedTickersTradeUpdates(Constants.SCREENED_TICKERS_NUMBER_OF_SYMBOLS);

        // Concurrent distribution for each ticker with a separate thread
        LocalDateTime currDateTime = LocalDateTime.now();
        int currentMinutes = currDateTime.getMinute();
        int tIMinutes = ((currentMinutes / 5) * 5) - 5;
        LocalDateTime localDateTime = LocalDateTime.of(currDateTime.getYear(), currDateTime.getMonth(), currDateTime.getDayOfMonth(), currDateTime.getHour(), tIMinutes,0);

        for(int i=0; i<tickersTradeUpdates.size(); i++){
            TechnicalIndicatorResponse technicalIndicatorResponse = tickerUtil.technicalIndicatorRetrieve(tickersTradeUpdates.get(i).getSymbol(), localDateTime);
            if(technicalIndicatorResponse != null && technicalIndicatorResponse.getValues() != null && !technicalIndicatorResponse.getValues().isEmpty())
                marketDataFunnel.processTickerTechnicalAnalysisUpdates(technicalIndicatorResponse);
            else
                log.warn("{}", technicalIndicatorResponse);

            if(i==Constants.TA_API_MAX_REQUESTS_PER_MIN)
                try {
                    Thread.sleep(Constants.TA_API_MAX_REQUESTS_REACHED_WAIT_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    public static double calculatePercentToDecimal(double percentRate) {
        return percentRate / 100;
    }

    public static double getMaximumPriceAction(float price) {
        return price * calculatePercentToDecimal(Constants.MAXIMUM_PRICE_ACTION_EXIT);
    }

//    public void sortScreenedTickers(){
//        List<ScreenedTickerDTO> screenedTickerDTOS = screenedTickersService.getScreenedTickers();
//
//        Map<String, ScreenedTickerDTO> screenedTickerDTOSMap = screenedTickerDTOS.stream().collect(Collectors.toMap(ScreenedTickerDTO::getSymbol, ScreenedTickerDTO->ScreenedTickerDTO));
//        Comparator<ScreenedTickerDTO> byExchangeAndBeta = (ScreenedTickerDTO tkr1, ScreenedTickerDTO tkr2) -> {
//            if(tkr1.getBeta().equals(tkr2.getBeta())) {
//                return tkr1.getExchangeShortName().compareTo(tkr2.getExchangeShortName());
//            } else
//                return tkr2.getBeta().compareTo(tkr1.getBeta());
//        };
//        List<String> screenedTickers = screenedTickerDTOSMap.entrySet().stream()
//                .sorted(Map.Entry.<String, ScreenedTickerDTO>comparingByValue(byExchangeAndBeta))
//                .map(x -> x.getKey() + " - " + x.getValue().getName() + " [" + x.getValue().getExchangeShortName() + "/" +   x.getValue().getSector() + "] (" + x.getValue().getBeta() +")")
//                .collect(Collectors.toList());
//        log.info("{}, {}", screenedTickers.size(), screenedTickers);
//    }

}
