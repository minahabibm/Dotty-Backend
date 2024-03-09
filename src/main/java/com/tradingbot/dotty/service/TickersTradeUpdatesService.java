package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.TickersTradeUpdates;
import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;
import com.tradingbot.dotty.models.dto.TickersTradeUpdatesDTO;

import java.util.List;

public interface TickersTradeUpdatesService {
    List<TickersTradeUpdatesDTO> getTickersTradeUpdates();

    List<TickersTradeUpdatesDTO> getSortedTickersTradeUpdates(int numberOfTickers);
    String insertTickersTradeUpdates(List<TickersTradeUpdates> tickersTradeUpdates);
    String insertTickerTradeUpdates(TickersTradeUpdatesDTO tickersTradeUpdatesDTO);
    String updateTickersTradeUpdates(TickersTradeUpdates tickersTradeUpdates);
    String deleteTickersTradeUpdates(TickersTradeUpdatesDTO tickersTradeUpdatesDTO);
    String deleteTickersTradeUpdates();
}
