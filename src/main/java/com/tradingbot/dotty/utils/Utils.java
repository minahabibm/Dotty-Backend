package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.TickersTradeUpdates;
import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;
import com.tradingbot.dotty.models.dto.ScreenedTickersResponse;
import com.tradingbot.dotty.models.dto.TechnicalIndicatorResponse;
import com.tradingbot.dotty.service.ScreenedTickersService;
import com.tradingbot.dotty.service.TickersTradeUpdatesService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j(topic = "Dotty_Utils")
@Service
public class Utils {

    @Autowired
    private ExternalApiRequests apiRequests;

    @Autowired
    private TickerUpdatesWebSocket tickerUpdatesWebSocket;

    @Autowired
    private ScreenedTickersService screenedTickersService;

    @Autowired
    private TickersTradeUpdatesService tickersTradeUpdatesService;

    @Autowired
    private ModelMapper modelMapper;

    public void stockScreenerUpdate() {
        log.info("Getting Screened Tickers.");
        ScreenedTickersResponse[] screenedTickersResponse = apiRequests.stockScreenerUpdateRetrieve();
        if (screenedTickersResponse != null) {
            log.info("Saving Tickers to Screened Tickers.");
            screenedTickersService.insertScreenedTickers(Arrays.stream(screenedTickersResponse).map(screenedTickers -> modelMapper.map(screenedTickers, ScreenedTicker.class)).collect(Collectors.toList()));

            selectAndSaveScreenedTickers();
        }
    }

    public void selectAndSaveScreenedTickers(){
        log.info("Getting Screened Tickers to market trades stream.");
        List<ScreenedTickerDTO> screenedTickerDTOS = screenedTickersService.getScreenedTickers();

        log.info("Filter Screened Tickers for Sectors");
        List<ScreenedTicker> screenedTickers = screenedTickerDTOS.stream()
                .filter(x -> (x.getSector()!=null && (x.getSector().contains("Consumer Cyclical") || x.getSector().contains("Technology") || x.getSector().contains("Communication Services"))))
                .map(x -> modelMapper.map(x, ScreenedTicker.class))
                .collect(Collectors.toList());

        log.info("Inserting Screened Tickers to market trade updates.");
        List<TickersTradeUpdates> tickersTradeUpdates = screenedTickers.stream().map(x -> modelMapper.map(x,TickersTradeUpdates.class)).collect(Collectors.toList());
        tickersTradeUpdatesService.insertTickersTradeUpdates(tickersTradeUpdates);
        log.info("{}", tickersTradeUpdatesService.getTickersTradeUpdates().size());

        log.info("Getting Sorted Tickers for Trade Updates");
        tickersTradeUpdatesService.getSortedTickersTradeUpdates(10);
    }

    public void tickersTechnicalAnalysis() {
        log.info("Getting Technical Analysis at " + LocalDateTime.now());
        // Get Ticker Trades Updates
        LocalDateTime localDateTime = LocalDateTime.of(2024,03,8, 15,50,00);
        TechnicalIndicatorResponse technicalIndicatorResponse = apiRequests.technicalIndicatorRetrieve("AAPL", localDateTime);
        System.out.println(technicalIndicatorResponse);

        // concurrent distribution for each ticker with a seprate process

        // each process will determine position entry\exit -> quote&trade and price updates sub/unsub once in trade.
    }

    public void subscribeToTickersTradesUpdate(String ticker) {
        try {
            log.info("Trades Update ::Subscribe Ticker:: {}");
            WebSocketSession tickerUpdatesWSSession = tickerUpdatesWebSocket.getTickerUpdatesWebSocket();
            tickerUpdatesWSSession.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"" + ticker + "\"}"));
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void unsubscribeToTickersTradesUpdate(String ticker) {
        try {
            log.info("Trades Update ::Unsubscribe Ticker:: {}");
            WebSocketSession tickerUpdatesWSSession = tickerUpdatesWebSocket.getTickerUpdatesWebSocket();
            tickerUpdatesWSSession.sendMessage(new TextMessage("{\"type\":\"unsubscribe\",\"symbol\":\"" + ticker + "\"}"));
//            tickerUpdatesWSSession.close();
//            System.out.println(tickerUpdatesWSSession.isOpen());
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
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
