package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.TickersTradeUpdates;
import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;
import com.tradingbot.dotty.models.dto.ScreenedTickersResponse;
import com.tradingbot.dotty.models.dto.TickersTradeUpdatesDTO;
import com.tradingbot.dotty.service.ScreenedTickersService;
import com.tradingbot.dotty.service.TickersTradeUpdatesService;
import com.tradingbot.dotty.serviceImpls.TickerUpdatesWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j(topic = "Dotty_Ticker_Utils")
@Service
public class Utils {

    @Value("${stock-screener-api.base-url}")
    private String baseUrlStockScreenerAPI;
    @Value("${stock-screener-api.APIkey}")
    private String APIkeyStockScreenerAPI;
    @Value("${stock-screener-api.query-params.country}")
    private String country;
    @Value("${stock-screener-api.query-params.market-cap-more-than}")
    private Long marketCapMoreThan;
    @Value("${stock-screener-api.query-params.exchange}")
    private String[] exchange;
    @Value("${stock-screener-api.query-params.beta-more-than}")
    private float betaMoreThan;
    @Value("${stock-screener-api.query-params.is-actively-trading}")
    private boolean isActivelyTrading;

    @Autowired
    private ScreenedTickersService screenedTickersService;

    @Autowired
    private TickersTradeUpdatesService tickersTradeUpdatesService;

    @Autowired
    private TickerUpdatesWebSocket tickerUpdatesWebSocket;

    @Autowired
    private ModelMapper modelMapper;


    public ScreenedTickersResponse[] stockScreenerUpdateRequest() {
        log.info("Stock Screening GET Request, with Criteria country {}, market cap more than {}, exchange {}," +
                        " beta more than {}, and is actively trading."
                , country, marketCapMoreThan, exchange, betaMoreThan);

        WebClient webClient = WebClient.builder().baseUrl(baseUrlStockScreenerAPI)
                .exchangeStrategies(ExchangeStrategies.builder().codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(500 * 1024)).build())
                .build();

        ScreenedTickersResponse[] screenedTickersResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("country", country)
                        .queryParam("marketCapMoreThan", marketCapMoreThan)
                        .queryParam("exchange", exchange[0])
                        .queryParam("exchange", exchange[1])
                        .queryParam("exchange", exchange[2])
                        .queryParam("betaMoreThan", betaMoreThan)
                        .queryParam("isActivelyTrading", isActivelyTrading)
                        .queryParam("apikey", APIkeyStockScreenerAPI)
                        .build())
                .retrieve()
                .bodyToMono(ScreenedTickersResponse[].class)
                .block();
//                .doOnNext(x -> System.out.println(x[0]))
//                .subscribe();

        log.info("Saving Tickers to Screened Tickers.");
        screenedTickersService.insertScreenedTickers(Arrays.stream(screenedTickersResponse).map(screenedTickers -> modelMapper.map(screenedTickers, ScreenedTicker.class)).collect(Collectors.toList()));


        return screenedTickersResponse;
    }

    public void tickersTechnicalAnalysis() {
        log.info("Task executed " + LocalDateTime.now());
        // Get Ticker Trades Updates

        // concurrent distribution for each ticker with a seprate process
        // each process will determine position entry\exit -> quote&trade and price updates sub/unsub once in trade.
    }

    public void subscribeToTickersTradesUpdate() {
        try {
            WebSocketSession tickerUpdatesWSSession = tickerUpdatesWebSocket.getTickerUpdatesWebSocket();
            tickerUpdatesWSSession.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"IWM\"}"));
//            tickerUpdatesWSSession.close();
//            System.out.println(tickerUpdatesWSSession.isOpen());
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
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
