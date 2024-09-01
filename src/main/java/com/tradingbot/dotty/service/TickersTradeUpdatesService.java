package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.TickersTradeUpdates;
import com.tradingbot.dotty.models.dto.TickersTradeUpdatesDTO;

import java.util.List;
import java.util.Optional;

public interface TickersTradeUpdatesService {
    List<TickersTradeUpdatesDTO> getTickersTradeUpdates();
    List<TickersTradeUpdatesDTO> getSortedTickersTradeUpdates(int numberOfTickers);
    List<TickersTradeUpdatesDTO> insertTickersTradeUpdates(List<TickersTradeUpdates> tickersTradeUpdates);
    Optional<TickersTradeUpdatesDTO> insertTickerTradeUpdates(TickersTradeUpdatesDTO tickersTradeUpdatesDTO);
    Optional<TickersTradeUpdatesDTO> updateTickersTradeUpdates(TickersTradeUpdates tickersTradeUpdates);
    void deleteTickerTradeUpdates(TickersTradeUpdatesDTO tickersTradeUpdatesDTO);
    void deleteTickersTradeUpdates();
}
