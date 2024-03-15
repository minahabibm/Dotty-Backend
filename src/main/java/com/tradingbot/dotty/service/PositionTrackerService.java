package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.PositionTrackerDTO;

import java.util.List;

public interface PositionTrackerService {

    List<PositionTrackerDTO> getPositionTrackers();
    PositionTrackerDTO getPositionTracker(Long Id);
    Long insertPositionTracker(PositionTrackerDTO positionTrackerDTO);
    Long updatePositionTracker(PositionTrackerDTO positionTrackerDTO);
    String deletePositionTracker();

}
