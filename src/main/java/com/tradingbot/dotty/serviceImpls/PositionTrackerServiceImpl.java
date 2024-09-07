package com.tradingbot.dotty.serviceImpls;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;
import com.tradingbot.dotty.models.PositionTracker;
import com.tradingbot.dotty.models.dto.PositionTrackerDTO;
import com.tradingbot.dotty.repositories.PositionTrackerRepository;
import com.tradingbot.dotty.service.PositionTrackerService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
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
    public Optional<PositionTrackerDTO> getPositionTracker(Long id) {
        log.trace(ENTITY_READ_OPERATION, id, "PositionTracker");
        return positionTrackerRepository.findById(id).map(positionTracker -> modelMapper.map(positionTracker, PositionTrackerDTO.class));
    }

    @Override
    public Optional<PositionTrackerDTO> getPositionTracker(String symbol) {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, "PositionTracker", "Symbol: "+ symbol+" and Active");
        return positionTrackerRepository.findBySymbolAndActiveTrue(symbol).map(positionTracker -> modelMapper.map(positionTracker, PositionTrackerDTO.class));
    }

    @Override
    public Optional<PositionTrackerDTO> insertPositionTracker(PositionTrackerDTO positionTrackerDTO) {
        log.trace(ENTITY_CREATE_OPERATION, positionTrackerDTO, "PositionTracker");
        PositionTracker positionTracker = positionTrackerRepository.save(modelMapper.map(positionTrackerDTO, PositionTracker.class));
        return Optional.of(modelMapper.map(positionTracker, PositionTrackerDTO.class));
    }

    @Override
    public Optional<PositionTrackerDTO> updatePositionTracker(PositionTrackerDTO positionTrackerDTO) {
        log.trace(ENTITY_UPDATE_OPERATION, positionTrackerDTO.getSymbol(), "PositionTracker");

        return positionTrackerRepository.findById(positionTrackerDTO.getPositionTrackerId())
                .map(existingPositionTracker -> {
                    BeanUtils.copyProperties(positionTrackerDTO, existingPositionTracker, "updatedAt");
                    PositionTracker updatedPositionTracker = positionTrackerRepository.save(existingPositionTracker);
                    return modelMapper.map(updatedPositionTracker, PositionTrackerDTO.class);
                });
    }

    @Override
    public void deletePositionTracker(Long positionTrackerId) {}
}
