package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;
import com.tradingbot.dotty.repositories.ScreenedTickersRepository;
import com.tradingbot.dotty.service.ScreenedTickersService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


// TODO Handle Exceptions
@Slf4j
@Service
public class ScreenedTickersServiceImpl implements ScreenedTickersService {

    @Autowired
    private ScreenedTickersRepository screenedTickersRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ScreenedTickerDTO> getScreenedTickers() {
        log.info("Getting Screened Tickers");
        return screenedTickersRepository.findAll().stream().map(screenedTicker -> modelMapper.map(screenedTicker, ScreenedTickerDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ScreenedTickerDTO> getTodayScreenedTickers() {
        Predicate<ScreenedTicker> localDateTimePredicate = (localDateTime) -> (localDateTime.getCreatedAt().isAfter(LocalDateTime.of(LocalDate.now(), LocalTime.ofSecondOfDay(0)))) || (localDateTime.getUpdatedAt().isAfter(LocalDateTime.of(LocalDate.now(), LocalTime.ofSecondOfDay(0))));
        return screenedTickersRepository.findAll().stream()
                .filter(localDateTimePredicate)
                .map(screenedTicker -> modelMapper.map(screenedTicker, ScreenedTickerDTO.class)).collect(Collectors.toList());
    }

    @Override
    public String insertScreenedTickers(List<ScreenedTicker> screenedTickers) {
        log.info("Inserting Screened Tickers");
//        screenedTickers.forEach(x -> x.setCreatedOn(LocalDateTime.now()));
        List<ScreenedTicker> screenedTickersList = screenedTickersRepository.saveAll(screenedTickers);
        return String.valueOf(screenedTickersList.size());
    }

    @Override
    public String insertScreenedTicker(ScreenedTickerDTO screenedTickerDTO) {
        log.info("Inserting Screened Tickers ");
        ScreenedTicker screenedTicker = screenedTickersRepository.save(modelMapper.map(screenedTickerDTO, ScreenedTicker.class));
        return String.valueOf(screenedTicker);
    }

    @Override
    public String updateScreenedTicker(ScreenedTicker screenedTicker) {
        log.info("Updating Screened Ticker ");
        ScreenedTicker screenedTickerUpdated = screenedTickersRepository.save(screenedTicker);
        return String.valueOf(screenedTickerUpdated);
    }

    @Override
    public String deleteScreenedTickers() {
        log.info("Deleting Screened Tickers ");
        screenedTickersRepository.deleteAll();
        return "";
    }
}
