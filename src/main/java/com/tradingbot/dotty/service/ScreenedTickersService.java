package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;

import java.util.List;

public interface ScreenedTickersService {
    List<ScreenedTickerDTO> getScreenedTickers();
    List<ScreenedTickerDTO> getTodayScreenedTickers();
    String insertAndUpdateScreenedTickers(List<ScreenedTickerDTO> ScreenedTickersDTOList);
    String insertScreenedTicker(ScreenedTickerDTO screenedTickerDTO);
    String updateScreenedTicker(ScreenedTickerDTO screenedTickerDTO);
    String deleteScreenedTickers();

}
