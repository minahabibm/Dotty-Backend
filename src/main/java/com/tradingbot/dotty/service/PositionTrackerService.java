package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.PositionTrackerDTO;

import java.util.List;
import java.util.Optional;

public interface PositionTrackerService {

    List<PositionTrackerDTO> getPositionTrackers();
    Optional<PositionTrackerDTO> getPositionTracker(Long Id);
    Optional<PositionTrackerDTO> getPositionTracker(String symbol);
    Optional<PositionTrackerDTO> insertPositionTracker(PositionTrackerDTO positionTrackerDTO);
    Optional<PositionTrackerDTO> updatePositionTracker(PositionTrackerDTO positionTrackerDTO);
    void deletePositionTracker(Long positionTrackerId);

}
