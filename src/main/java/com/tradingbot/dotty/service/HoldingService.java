package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.HoldingDTO;

import java.util.List;

public interface HoldingService {
    List<HoldingDTO> getHoldings();
    Long insertHolding(HoldingDTO holdingDTO);
    Long updateHolding(HoldingDTO holdingDTO);
    String deleteHolding();
}
