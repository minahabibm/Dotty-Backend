package com.tradingbot.dotty.serviceImpls;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;
import com.tradingbot.dotty.models.TickersTradeUpdates;
import com.tradingbot.dotty.models.dto.TickersTradeUpdatesDTO;
import com.tradingbot.dotty.repositories.TickersTradeUpdatesRepository;
import com.tradingbot.dotty.service.TickersTradeUpdatesService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TickersTradeUpdatesServiceImpl implements TickersTradeUpdatesService {

    @Autowired
    private TickersTradeUpdatesRepository tickersTradeUpdatesRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<TickersTradeUpdatesDTO> getTickersTradeUpdates() {
        log.trace(ENTITIES_READ_OPERATION, "TickersTradeUpdates");
        return tickersTradeUpdatesRepository.findAll().stream().map(tickersTradeUpdates -> modelMapper.map(tickersTradeUpdates, TickersTradeUpdatesDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<TickersTradeUpdatesDTO> getSortedTickersTradeUpdates(int numberOfTickers) {
        log.trace(ENTITIES_READ_OPERATION, "TickersTradeUpdates");

        log.info("Sorting Screened Tickers for Sectors and getting the first {}.", numberOfTickers);
        Comparator<TickersTradeUpdates> byExchangeAndBeta = (TickersTradeUpdates tkr1, TickersTradeUpdates tkr2) -> {
            if(Float.valueOf(tkr1.getScreenedTicker().getBeta()).equals(tkr2.getScreenedTicker().getBeta())) {
                return tkr1.getScreenedTicker().getExchangeShortName().compareTo(tkr2.getScreenedTicker().getExchangeShortName());
            } else
                return Float.compare(tkr2.getScreenedTicker().getBeta(), tkr1.getScreenedTicker().getBeta());
        };
        List<TickersTradeUpdatesDTO> sortedTickersTrades = tickersTradeUpdatesRepository.findAll().stream()
                .sorted(byExchangeAndBeta)
                .limit(numberOfTickers)
//                .peek(tickersTradeUpdatesDAO -> log.warn(String.valueOf(tickersTradeUpdatesDAO.getScreenedTicker())))
                .map(sortedTickerTrades -> modelMapper.map(sortedTickerTrades, TickersTradeUpdatesDTO.class))
                .collect(Collectors.toList());

        log.info("Sorted Tickers size: {} and list: {}",sortedTickersTrades.size(), sortedTickersTrades);
        return sortedTickersTrades;
    }

    @Override
    public List<TickersTradeUpdatesDTO> insertTickersTradeUpdates(List<TickersTradeUpdates> tickersTradeUpdates) {
        log.trace(ENTITIES_CREATE_OPERATION, "TickersTradeUpdates");

        List<TickersTradeUpdates> tickersTradeUpdatesList = tickersTradeUpdates.stream()
                .map(tickersTradeUpdate -> {
                    Optional<TickersTradeUpdates> ticker = tickersTradeUpdatesRepository.findBySymbol(tickersTradeUpdate.getSymbol());
                    if(ticker.isPresent()) {
                        log.info(ENTITY_CREATE_OPERATION, ticker, "ScreenedTicker");
                        BeanUtils.copyProperties(ticker.get(), tickersTradeUpdate,"updatedAt");
                    }
                    return tickersTradeUpdate;
                })
                .collect(Collectors.toList());

        return tickersTradeUpdatesRepository.saveAll(tickersTradeUpdatesList).stream()
                .map(tickerTradeUpdates -> modelMapper.map(tickerTradeUpdates, TickersTradeUpdatesDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TickersTradeUpdatesDTO> insertTickerTradeUpdates(TickersTradeUpdatesDTO tickersTradeUpdatesDTO) {
        return Optional.empty();
    }

    @Override
    public Optional<TickersTradeUpdatesDTO> updateTickersTradeUpdates(TickersTradeUpdates tickersTradeUpdates) {
        return Optional.empty();
    }

    @Override
    public void deleteTickerTradeUpdates(TickersTradeUpdatesDTO tickersTradeUpdatesDTO) {}

    @Override
    public void deleteTickersTradeUpdates() {}
}
