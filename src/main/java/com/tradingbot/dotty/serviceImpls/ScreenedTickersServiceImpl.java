package com.tradingbot.dotty.serviceImpls;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;
import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;
import com.tradingbot.dotty.repositories.ScreenedTickersRepository;
import com.tradingbot.dotty.service.ScreenedTickersService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ScreenedTickersServiceImpl implements ScreenedTickersService {

    @Autowired
    private ScreenedTickersRepository screenedTickersRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ScreenedTickerDTO> getScreenedTickers() {
        log.trace(ENTITIES_READ_OPERATION, "ScreenedTicker");
        return screenedTickersRepository.findAll().stream().map(screenedTicker -> modelMapper.map(screenedTicker, ScreenedTickerDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ScreenedTickerDTO> getTodayScreenedTickers() {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, "ScreenedTicker", "Today "+LocalDate.now());
        Predicate<ScreenedTicker> localDateTimePredicate = (localDateTime) -> (localDateTime.getCreatedAt().isAfter(LocalDateTime.of(LocalDate.now(), LocalTime.ofSecondOfDay(0)))) || (localDateTime.getUpdatedAt().isAfter(LocalDateTime.of(LocalDate.now(), LocalTime.ofSecondOfDay(0))));
        return screenedTickersRepository.findAll().stream()
                .filter(localDateTimePredicate)
                .map(screenedTicker -> modelMapper.map(screenedTicker, ScreenedTickerDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ScreenedTickerDTO> insertAndUpdateScreenedTickers(List<ScreenedTickerDTO> ScreenedTickersDTOList) {
        log.trace(ENTITIES_CREATE_OPERATION, "ScreenedTicker");
        List<ScreenedTicker> screenedTickerList = ScreenedTickersDTOList.stream()
                .map(screenedTickerDTO -> modelMapper.map(screenedTickerDTO, ScreenedTicker.class))
                .map(screenedTicker -> {
                    Optional<ScreenedTicker> ticker = screenedTickersRepository.findBySymbol(screenedTicker.getSymbol());
                    if(ticker.isPresent()) {
                        log.info(ENTITY_CREATE_OPERATION, ticker, "ScreenedTicker");
                        BeanUtils.copyProperties(ticker.get(), screenedTicker, "updatedAt");
                    }
                    return screenedTicker;
                })
                .collect(Collectors.toList());

            return screenedTickersRepository.saveAll(screenedTickerList).stream()
                    .map(screenedTicker -> modelMapper.map(screenedTicker, ScreenedTickerDTO.class))
                    .collect(Collectors.toList());
    }

    @Override
    public Optional<ScreenedTickerDTO> insertScreenedTicker(ScreenedTickerDTO screenedTickerDTO) {
        log.trace(ENTITY_CREATE_OPERATION, screenedTickerDTO, "ScreenedTicker");
        ScreenedTicker screenedTicker = screenedTickersRepository.save(modelMapper.map(screenedTickerDTO, ScreenedTicker.class));
        return Optional.of(modelMapper.map(screenedTicker, ScreenedTickerDTO.class));
    }

    @Override
    public Optional<ScreenedTickerDTO> updateScreenedTicker(ScreenedTickerDTO screenedTickerDTO) {
        log.trace(ENTITY_UPDATE_OPERATION, screenedTickerDTO.getName(), "ScreenedTicker");
        return screenedTickersRepository.findBySymbol(screenedTickerDTO.getSymbol())
                .map(existingScreenedTicker -> {
                    BeanUtils.copyProperties(screenedTickerDTO, existingScreenedTicker, "updatedAt");
                    ScreenedTicker updatedScreenedTickerUpdated = screenedTickersRepository.save(existingScreenedTicker);
                    return modelMapper.map(updatedScreenedTickerUpdated, ScreenedTickerDTO.class);
                });
    }

    @Override
    public void deleteScreenedTickers() {
        log.trace(ENTITIES_DELETE_OPERATION, "ScreenedTicker");
        screenedTickersRepository.deleteAll();
    }
}
