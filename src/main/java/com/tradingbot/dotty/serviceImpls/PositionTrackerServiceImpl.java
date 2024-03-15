package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.PositionTracker;
import com.tradingbot.dotty.models.dto.PositionTrackerDTO;
import com.tradingbot.dotty.repositories.PositionTrackerRepository;
import com.tradingbot.dotty.service.PositionTrackerService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PositionTrackerServiceImpl implements PositionTrackerService {

    @Autowired
    private PositionTrackerRepository positionTrackerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<PositionTrackerDTO> getPositionTrackers() {
        log.info("Getting Tickers Tracker Positions");
        return positionTrackerRepository.findAll().stream().map(positionTracker -> modelMapper.map(positionTracker, PositionTrackerDTO.class)).collect(Collectors.toList());
    }

    @Override
    public PositionTrackerDTO getPositionTracker(Long Id) {
        log.info("Getting Tickers Tracker Positions");
        Optional<PositionTracker> positionTracker = positionTrackerRepository.findById(Id);
        if(positionTracker.isPresent())
            return modelMapper.map(positionTracker.get(), PositionTrackerDTO.class);
        else
            throw new RuntimeException();
    }

    @Override
    public PositionTrackerDTO getTickerActivePositionTracker(String symbol) {
        log.info("Getting Tickers Tracker Positions");
        PositionTracker positionTracker = positionTrackerRepository.findBySymbolAndActiveTrue(symbol);
        PositionTrackerDTO positionTrackerDTO = null;
        if(positionTracker != null)
            positionTrackerDTO = modelMapper.map(positionTracker, PositionTrackerDTO.class);
        return positionTrackerDTO;
    }

    @Override
    public Long insertPositionTracker(PositionTrackerDTO positionTrackerDTO) {
        log.info("Inserting Ticker Tracker position");
        PositionTracker positionTracker = positionTrackerRepository.save(modelMapper.map(positionTrackerDTO, PositionTracker.class));
        return positionTracker.getPositionTrackerId();
    }

    @Override
    public Long updatePositionTracker(PositionTrackerDTO positionTrackerDTO) {
        log.info("Updating Ticker Tracker position");
        PositionTracker positionTracker = positionTrackerRepository.save(modelMapper.map(positionTrackerDTO, PositionTracker.class));
        return positionTracker.getPositionTrackerId();
    }

    @Override
    public String deletePositionTracker() {
        return null;
    }
}
