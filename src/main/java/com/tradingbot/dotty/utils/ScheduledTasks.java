package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;
import com.tradingbot.dotty.models.dto.ScreenedTickersResponse;
import com.tradingbot.dotty.service.ScreenedTickersService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;


// TODO Handle Exceptions
@Slf4j
@Service
public class ScheduledTasks {

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
    private ModelMapper modelMapper;

//    @Scheduled(fixedRate = 1000)
    @Scheduled(cron = "0 0 8 * * MON,TUE,WED,THU,FRI,SAT,SUN")
    public void StockScreener() {
        log.info("Scheduled Stock Screening with Criteria country {}, market cap more than {}, exchange {}," +
                        " beta more than {}, and is actively trading."
                ,country, marketCapMoreThan, exchange, betaMoreThan);

        WebClient webClient = WebClient.builder().baseUrl(baseUrlStockScreenerAPI)
                .exchangeStrategies(ExchangeStrategies.builder().codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(500 * 1024)).build())
                .build();

        ScreenedTickersResponse[] ScreenedTickers = webClient.get()
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

        if (ScreenedTickers != null) {
            screenedTickersService.insertScreenedTickers(Arrays.stream(ScreenedTickers).map(x -> modelMapper.map(x, ScreenedTicker.class)).collect(Collectors.toList()));
            log.info(sortScreenedTickers().toString());
        }

    }

    public List<String> sortScreenedTickers(){
        List<ScreenedTickerDTO> screenedTickerDTOS = screenedTickersService.getScreenedTickers();
        Map<String, ScreenedTickerDTO> screenedTickerDTOSMap = screenedTickerDTOS.stream().collect(Collectors.toMap(ScreenedTickerDTO::getSymbol, ScreenedTickerDTO->ScreenedTickerDTO));

        Comparator<ScreenedTickerDTO> byExchangeAndBeta = (ScreenedTickerDTO tkr1, ScreenedTickerDTO tkr2) -> {
            if(tkr1.getBeta().equals(tkr2.getBeta())) {
                return tkr1.getExchangeShortName().compareTo(tkr2.getExchangeShortName());
            } else
                return tkr2.getBeta().compareTo(tkr1.getBeta());
        };

        return screenedTickerDTOSMap.entrySet().stream()
                .sorted(Map.Entry.<String, ScreenedTickerDTO>comparingByValue(byExchangeAndBeta))
                .map(x -> x.getKey()+ " - " + x.getValue().getName() + " [" + x.getValue().getExchangeShortName() + "] (" + x.getValue().getBeta() +")")
                .collect(Collectors.toList());
    }

}
