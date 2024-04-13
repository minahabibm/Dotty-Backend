package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.PositionDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface PositionService {

    List<PositionDTO> getPositions();
    PositionDTO getPositionBySymbolAndIntervals(String symbol, LocalDateTime interval);
    List<PositionDTO> getSortedActiveTickerPositions(String symbol, Long positionTrackerID);
    List<Long> insertPositions(List<PositionDTO> positionDTOList);
    Long insertPosition(PositionDTO positionDTO);
    Long updatePosition(PositionDTO positionDTO);
    String deletePosition();

}
