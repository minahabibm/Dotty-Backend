package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.HoldingDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HoldingService {

    List<HoldingDTO> getHoldings();
    Optional<HoldingDTO> insertHolding(HoldingDTO holdingDTO);
    Optional<HoldingDTO> updateHolding(HoldingDTO holdingDTO);
    void deleteHolding();

    Map<String, Integer> compareHoldings();
    List<HoldingDTO> getHoldingsStatistics();
}
