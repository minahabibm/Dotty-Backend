package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;

import java.util.List;

public interface ScreenedTickersService {
    List<ScreenedTickerDTO> getScreenedTickers();
    String insertScreenedTickers(List<ScreenedTicker> screenedTicker);
    String insertScreenedTicker(ScreenedTickerDTO screenedTickerDTO);
    String updateScreenedTicker(ScreenedTicker screenedTickers);
    String deleteScreenedTickers();
}
