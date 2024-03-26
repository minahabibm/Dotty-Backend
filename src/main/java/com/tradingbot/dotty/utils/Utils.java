package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.TickersTradeUpdates;
import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;
import com.tradingbot.dotty.models.dto.ScreenedTickersResponse;
import com.tradingbot.dotty.models.dto.TickersTradeUpdatesDTO;
import com.tradingbot.dotty.service.ScreenedTickersService;
import com.tradingbot.dotty.service.TickersTradeUpdatesService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class Utils {

    @Autowired
    private ExternalApiRequests apiRequests;

    @Autowired
    private ScreenedTickersService screenedTickersService;

    @Autowired
    private TickersTradeUpdatesService tickersTradeUpdatesService;

    @Autowired
    private ConcurrentMarketDataFunnel marketDataFunnel;

    @Autowired
    private ModelMapper modelMapper;

    public void stockScreenerUpdate() {
        log.info("Getting Screened Tickers.");
        ScreenedTickersResponse[] screenedTickersResponse = apiRequests.stockScreenerUpdateRetrieve();
        if (screenedTickersResponse != null) {
            log.info("Saving Tickers {} to Screened Tickers. ", screenedTickersResponse);
            screenedTickersService.insertScreenedTickers(Arrays.stream(screenedTickersResponse).map(screenedTicker -> modelMapper.map(screenedTicker, ScreenedTickerDTO.class)).collect(Collectors.toList()));

            selectAndSaveScreenedTickers();
        }
    }

    public void selectAndSaveScreenedTickers(){
        log.info("Getting Screened Tickers to market trades stream.");
        List<ScreenedTickerDTO> screenedTickerDTOS = screenedTickersService.getTodayScreenedTickers();

        log.info("Filter Screened Tickers for Sectors");
        List<ScreenedTicker> screenedTickers = screenedTickerDTOS.stream()
                .filter(x -> (x.getSector()!=null && (x.getSector().contains("Consumer Cyclical") || x.getSector().contains("Technology") || x.getSector().contains("Communication Services"))))
                .map(x -> modelMapper.map(x, ScreenedTicker.class))
                .toList();

        log.info("Inserting Screened Tickers to market trade updates.");
        List<TickersTradeUpdates> tickersTradeUpdates = screenedTickers.stream().map(x -> modelMapper.map(x,TickersTradeUpdates.class)).collect(Collectors.toList());
        tickersTradeUpdatesService.insertTickersTradeUpdates(tickersTradeUpdates);
        log.info("{}", tickersTradeUpdatesService.getTickersTradeUpdates().size());
    }

    public void tickersTechnicalAnalysis() {
        log.info("Getting Technical Analysis at " + LocalDateTime.now());

        // Get Ticker Trades Updates
        log.info("Getting Sorted Tickers for Trade Updates");
        List<TickersTradeUpdatesDTO> tickersTradeUpdates = tickersTradeUpdatesService.getSortedTickersTradeUpdates(Constants.SCREENED_TICKERS_NUMBER_OF_SYMBOLS);

        // concurrent distribution for each ticker with a separate Thread
        LocalDateTime currDateTime = LocalDateTime.now();
        LocalDateTime localDateTime = LocalDateTime.of(currDateTime.getYear(), currDateTime.getMonth(), currDateTime.getDayOfMonth(), currDateTime.getHour(), currDateTime.getMinute(),0);

        LocalDateTime dateTime = LocalDateTime.of(2024,3,15, 9,30,0);

        for(int i=0; i<tickersTradeUpdates.size(); i++){
            marketDataFunnel.processTickerTechnicalAnalysisUpdates(apiRequests.technicalIndicatorRetrieve(tickersTradeUpdates.get(i).getSymbol(), dateTime));
            if(i==Constants.TA_API_MAX_REQUESTS_PER_MIN)
                try {
                    Thread.sleep(Constants.TA_API_MAX_REQUESTS_REACHED_WAIT_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }
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
