package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.Position;
import com.tradingbot.dotty.models.dto.PositionDTO;
import com.tradingbot.dotty.repositories.PositionRepository;
import com.tradingbot.dotty.service.PositionService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        log.info("Getting Tickers Position");
        return positionRepository.findAll().stream().map(position -> modelMapper.map(position, PositionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<PositionDTO> getSortedActiveTickerPositions(String symbol, Long positionTrackerID) {
        log.info("Getting Active Ticker {} Positions", symbol);
        return positionRepository.findBySymbolAndPositionTracker_PositionTrackerIdOrderByIntervalsDesc(symbol, positionTrackerID).stream().map(position -> modelMapper.map(position, PositionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<Long> insertPositions(List<PositionDTO> positions) {
        log.info("Inserting tickers position");
        List<Position> tickersPositionList = positions.stream().map(positionDTO -> modelMapper.map(positionDTO, Position.class)).collect(Collectors.toList());
        List<Position> tickersPositionsList = positionRepository.saveAll(tickersPositionList);
        return tickersPositionsList.stream().map(Position::getPositionId).collect(Collectors.toList());
    }

    @Override
    public Long insertPosition(PositionDTO positionDTO) {
        log.info("Inserting Ticker position ");
        Position position = positionRepository.save(modelMapper.map(positionDTO, Position.class));
        return position.getPositionId();
    }

    @Override
    public Long updatePosition(PositionDTO positionDTO) {
        log.info("Updating Ticker position ");
        Position position = positionRepository.save(modelMapper.map(positionDTO, Position.class));
        return position.getPositionId();
    }

    @Override
    public String deletePosition() {
        return null;
    }
}
