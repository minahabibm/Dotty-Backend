package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.PositionDTO;

import java.util.List;

public interface PositionService {

    List<PositionDTO> getPositions();
    List<PositionDTO> getSortedActiveTickerPositions(String symbol, Long positionTrackerID);
    List<Long> insertPositions(List<PositionDTO> positionDTOList);
    Long insertPosition(PositionDTO positionDTO);
    Long updatePosition(PositionDTO positionDTO);
    String deletePosition();

}
