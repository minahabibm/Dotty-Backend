package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.PositionDTO;

import java.util.List;

public interface PositionService {

    List<PositionDTO> getPosition();
    List<PositionDTO> getActivePositions(String symbol);
    List<Long> insertPositions(List<PositionDTO> positionDTOList);
    Long insertPosition(PositionDTO positionDTO);
    Long updatePosition(PositionDTO positionDTO);
    String deletePosition();

}
