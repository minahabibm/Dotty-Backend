package com.tradingbot.dotty.serviceImpls;

import static com.tradingbot.dotty.utils.LoggingConstants.*;
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
        log.trace(ENTITIES_READ_OPERATION, "PositionTracker");
        return positionTrackerRepository.findAll().stream().map(positionTracker -> modelMapper.map(positionTracker, PositionTrackerDTO.class)).collect(Collectors.toList());
    }

    @Override
    public PositionTrackerDTO getPositionTracker(Long id) {
        log.trace(ENTITY_READ_OPERATION, id, "PositionTracker");
        Optional<PositionTracker> positionTracker = positionTrackerRepository.findById(id);
        if(positionTracker.isPresent())
            return modelMapper.map(positionTracker.get(), PositionTrackerDTO.class);
        else
            throw new RuntimeException();
    }

    @Override
    public PositionTrackerDTO getTickerActivePositionTracker(String symbol) {
        log.trace(ENTITIES_READ_WITH_FILERS_OPERATION, "PositionTracker", "Symbol: "+ symbol+" and Active");
        PositionTracker positionTracker = positionTrackerRepository.findBySymbolAndActiveTrue(symbol);
        log.debug("position tracker {}", positionTracker);
        PositionTrackerDTO positionTrackerDTO = null;
        if(positionTracker != null)
            positionTrackerDTO = modelMapper.map(positionTracker, PositionTrackerDTO.class);
        return positionTrackerDTO;
    }

    @Override
    public Long insertPositionTracker(PositionTrackerDTO positionTrackerDTO) {
        log.trace(ENTITY_CREATE_OPERATION, positionTrackerDTO, "PositionTracker");
        PositionTracker positionTracker = positionTrackerRepository.save(modelMapper.map(positionTrackerDTO, PositionTracker.class));
        return positionTracker.getPositionTrackerId();
    }

    @Override
    public Long updatePositionTracker(PositionTrackerDTO positionTrackerDTO) {
        log.trace(ENTITY_UPDATE_OPERATION, positionTrackerDTO.getSymbol(), "PositionTracker");
        PositionTracker positionTracker = positionTrackerRepository.save(modelMapper.map(positionTrackerDTO, PositionTracker.class));
        return positionTracker.getPositionTrackerId();
    }

    @Override
    public String deletePositionTracker() {
        return null;
    }
}
