package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.TickersTradeUpdates;
import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;
import com.tradingbot.dotty.models.dto.requests.FMP.QuoteOrder;
import com.tradingbot.dotty.models.dto.requests.MarketHoursResponse;
import com.tradingbot.dotty.models.dto.requests.FMP.ScreenedTickersResponse;
import com.tradingbot.dotty.models.dto.requests.TechnicalIndicatorResponse;
import com.tradingbot.dotty.models.dto.TickersTradeUpdatesDTO;
import com.tradingbot.dotty.service.ScreenedTickersService;
import com.tradingbot.dotty.service.TickersTradeUpdatesService;
import com.tradingbot.dotty.utils.ExternalApi.TickerUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;
import static com.tradingbot.dotty.utils.constants.Constants.*;


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
    private CacheManager cacheManager;

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
        LocalTime localTime = LocalTime.now();
        Optional<MarketHoursResponse.EventData> todayMarketStatus = getMarketHolidays();
        if (todayMarketStatus.isPresent()) {
            String[] tradingHours = todayMarketStatus.get().getTradingHour().split("-");
            if(tradingHours.length == 0) {
                log.warn(MARKET_CLOSED_FOR_HOLIDAY, todayMarketStatus.get().getEventName());
            } else {
                String[] startPolling = tradingHours[0].split(":");
                String[] endPolling = tradingHours[1].split(":");
                int startHour = Integer.parseInt(startPolling[0]);
                int startMinute = Integer.parseInt(startPolling[1]);
                int endHour = Integer.parseInt(endPolling[0]);
                int endMinute = Integer.parseInt(endPolling[1]);

                if(localTime.isAfter(LocalTime.of(startHour, startMinute)) && localTime.isBefore(LocalTime.of(endHour, endMinute)))
                    tickersTechnicalAnalysis();
            }
        } else {
            if(localTime.isAfter(LocalTime.of(TA_API_START_POLLING_HOUR, TA_API_START_POLLING_MINUTE)) && localTime.isBefore(LocalTime.of(TA_API_STOP_POLLING_HOUR, TA_API_STOP_POLLING_MINUTE)))
                tickersTechnicalAnalysis();
        }

//        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//        Runnable task = () -> tickersTechnicalAnalysis();
//        ScheduledFuture<?> schedulerHandle = executor.scheduleAtFixedRate(task, 0, Constants.TA_API_POLLING_RATE, TimeUnit.SECONDS);
//        Runnable canceller = () -> {
//            log.info(SCHEDULED_TASK_END, LocalDateTime.now());
//            schedulerHandle.cancel(false);
//            executor.shutdown(); // <---- Now the call is within the `canceller` Runnable.
//        };
//        long seconds = ChronoUnit.SECONDS.between(LocalTime.now(), LocalTime.of(Constants.TA_API_STOP_POLLING_HOUR, Constants.TA_API_STOP_POLLING_MINUTE));
//        executor.schedule(canceller, seconds, TimeUnit.SECONDS);
    }

    public void tickersTechnicalAnalysis() {
        log.debug(TICKER_TECHNICAL_ANALYSIS, LocalDateTime.now());

        // Get Ticker Trades Updates
        log.debug(TICKER_TECHNICAL_ANALYSIS_SORTED_TICKERS);
        List<TickersTradeUpdatesDTO> tickersTradeUpdates = tickersTradeUpdatesService.getSortedTickersTradeUpdates(SCREENED_TICKERS_NUMBER_OF_SYMBOLS);

        // Concurrent distribution for each ticker with a separate thread
        for(int i=0; i<tickersTradeUpdates.size(); i++){
            TechnicalIndicatorResponse technicalIndicatorResponse = tickerUtil.technicalIndicatorRetrieve(tickersTradeUpdates.get(i).getSymbol(), getRoundedDateTimeTo5MinInterval());
            if(technicalIndicatorResponse != null && technicalIndicatorResponse.getValues() != null && !technicalIndicatorResponse.getValues().isEmpty())
                marketDataFunnel.processTickerTechnicalAnalysisUpdates(technicalIndicatorResponse);
            else
                log.warn("{}", technicalIndicatorResponse);

            if(i == TA_API_MAX_REQUESTS_PER_MIN)
                try {
                    Thread.sleep(TA_API_MAX_REQUESTS_REACHED_WAIT_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    public QuoteOrder[] getTickerCurrentQuote(String ticker) {
        return tickerUtil.tickerQuoteRetrieve(ticker);
    }

    public Optional<MarketHoursResponse.EventData> getMarketHolidays() {
        MarketHoursResponse marketHoursResponse = tickerUtil.marketHoursResponseRetrieve();
        LocalDate date = LocalDate.parse(marketHoursResponse.getData()[0].getAtDate());
        LocalDate localDate = LocalDate.now();
        if(localDate.getYear() > date.getYear()) {
            cacheManager.getCache("market").evictIfPresent("marketHolidays");
            marketHoursResponse = tickerUtil.marketHoursResponseRetrieve();
        }
        return Arrays.stream(marketHoursResponse.getData()).filter(eventData -> LocalDate.parse(eventData.getAtDate()).isEqual(localDate)).findFirst();
    }

    public static LocalDateTime getRoundedDateTimeTo5MinInterval() {
        LocalDateTime currDateTime = LocalDateTime.now();
        int currentMinutes = currDateTime.getMinute();
        int tIMinutes = ((currentMinutes / 5) * 5) - 5;

        if (tIMinutes < 0) {                                                                                            // Adjust tIMinutes to prevent negative values
            tIMinutes = 55;                                                                                             // previous hour's last 5-minute mark
            currDateTime = currDateTime.minusHours(1);
        }
        return LocalDateTime.of(currDateTime.getYear(), currDateTime.getMonth(), currDateTime.getDayOfMonth(), currDateTime.getHour(), tIMinutes,0);
    }

    public static double calculatePercentToDecimal(double percentRate) {
        if (percentRate == 0) return 0.0;
        return percentRate / 100;
    }

    public static double calculateRounded2DecimalPlaces(Double availableToTrade) {
        BigDecimal bigDecimal = new BigDecimal(availableToTrade *  0.1);
        bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    public static double getMaximumPriceAction(float price) {
        return price * calculatePercentToDecimal(MAXIMUM_PRICE_ACTION_EXIT);
    }

    public static double getAveragePrice(double totalCost,  double totalQuantity) {
        if (totalQuantity == 0) return 0.0;
        return totalCost / totalQuantity;
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
