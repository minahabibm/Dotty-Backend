package com.tradingbot.dotty.serviceImpls;

import static com.tradingbot.dotty.utils.LoggingConstants.*;
import com.tradingbot.dotty.models.Position;
import com.tradingbot.dotty.models.dto.PositionDTO;
import com.tradingbot.dotty.repositories.PositionRepository;
import com.tradingbot.dotty.service.PositionService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    public PositionDTO getPositionBySymbolAndIntervals(String symbol, LocalDateTime interval) {
        Optional<Position> position = positionRepository.findBySymbolAndIntervals(symbol, interval);
        PositionDTO positionDTO = null;
        if(position.isPresent())
            positionDTO = modelMapper.map(position.get(), PositionDTO.class);
        return positionDTO;
    }

    @Override
    public List<PositionDTO> getSortedActiveTickerPositions(String symbol, Long positionTrackerID) {
        log.trace(ENTITIES_READ_WITH_FILERS_OPERATION, "Position", "Symbol: "+ symbol +" Sorted and Active");
        return positionRepository.findBySymbolAndPositionTracker_PositionTrackerIdOrderByIntervalsDesc(symbol, positionTrackerID).stream().map(position -> modelMapper.map(position, PositionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<Long> insertPositions(List<PositionDTO> positions) {
        log.trace(ENTITIES_CREATE_OPERATION, "Position");
        List<Position> tickersPositionList = positions.stream().map(positionDTO -> modelMapper.map(positionDTO, Position.class)).collect(Collectors.toList());
        List<Position> tickersPositionsList = positionRepository.saveAll(tickersPositionList);
        return tickersPositionsList.stream().map(Position::getPositionId).collect(Collectors.toList());
    }

    @Override
    public Long insertPosition(PositionDTO positionDTO) {
        log.trace(ENTITY_CREATE_OPERATION, positionDTO, "Position");
        Position position = positionRepository.save(modelMapper.map(positionDTO, Position.class));
        return position.getPositionId();
    }

    @Override
    public Long updatePosition(PositionDTO positionDTO) {
        log.trace(ENTITY_UPDATE_OPERATION, positionDTO.getSymbol(), "PositionTracker");
        Position position = positionRepository.save(modelMapper.map(positionDTO, Position.class));
        return position.getPositionId();
    }

    @Override
    public String deletePosition() {
        return null;
    }
}
