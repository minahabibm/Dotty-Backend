package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.PositionDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PositionService {

    List<PositionDTO> getPositions();
    List<PositionDTO> getSortedActiveTickerPositions(String symbol, Long positionTrackerID);
    Optional<PositionDTO> getPositionBySymbolAndIntervals(String symbol, LocalDateTime interval);
    List<PositionDTO> insertPositions(List<PositionDTO> positionDTOList);
    Optional<PositionDTO> insertPosition(PositionDTO positionDTO);
    Optional<PositionDTO>  updatePosition(PositionDTO positionDTO);
    void deletePosition();

}
