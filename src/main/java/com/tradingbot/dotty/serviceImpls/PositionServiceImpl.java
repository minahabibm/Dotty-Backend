package com.tradingbot.dotty.serviceImpls;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;
import com.tradingbot.dotty.models.Position;
import com.tradingbot.dotty.models.dto.PositionDTO;
import com.tradingbot.dotty.repositories.PositionRepository;
import com.tradingbot.dotty.service.PositionService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PositionServiceImpl implements PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<PositionDTO> getPositions() {
        log.trace(ENTITIES_READ_OPERATION, "Position");
        return positionRepository.findAll().stream().map(position -> modelMapper.map(position, PositionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<PositionDTO> getSortedActiveTickerPositions(String symbol, Long positionTrackerID) {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, "Position", "Symbol: "+ symbol +" Sorted and Active");
        return positionRepository.findBySymbolAndPositionTracker_PositionTrackerIdOrderByIntervalsDesc(symbol, positionTrackerID).stream().map(position -> modelMapper.map(position, PositionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Optional<PositionDTO> getPositionBySymbolAndIntervals(String symbol, LocalDateTime interval) {
        return positionRepository.findBySymbolAndIntervals(symbol, interval).map(position -> modelMapper.map(position, PositionDTO.class));
    }

    @Override
    public List<PositionDTO> insertPositions(List<PositionDTO> positions) {
        log.trace(ENTITIES_CREATE_OPERATION, "Position");
        List<Position> tickersPositionList = positions.stream().map(positionDTO -> modelMapper.map(positionDTO, Position.class)).collect(Collectors.toList());
        return positionRepository.saveAll(tickersPositionList).stream().map(Position -> modelMapper.map(Position, PositionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Optional<PositionDTO> insertPosition(PositionDTO positionDTO) {
        log.trace(ENTITY_CREATE_OPERATION, positionDTO, "Position");
        Position position = positionRepository.save(modelMapper.map(positionDTO, Position.class));
        return Optional.of(modelMapper.map(position, PositionDTO.class));
    }

    @Override
    public Optional<PositionDTO> updatePosition(PositionDTO positionDTO) {
        log.trace(ENTITY_UPDATE_OPERATION, positionDTO.getSymbol(), "PositionTracker");
        return positionRepository.findById(positionDTO.getPositionId())
                .map(existingPosition -> {
                    BeanUtils.copyProperties(positionDTO, existingPosition, "updatedAt");
                    Position updatedPosition = positionRepository.save(existingPosition);
                    return modelMapper.map(updatedPosition, PositionDTO.class);
                });
    }

    @Override
    public void deletePosition() {

    }

}
