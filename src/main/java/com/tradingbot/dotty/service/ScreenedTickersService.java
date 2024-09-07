package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;

import java.util.List;
import java.util.Optional;

public interface ScreenedTickersService {
    List<ScreenedTickerDTO> getScreenedTickers();
    List<ScreenedTickerDTO> getTodayScreenedTickers();
    List<ScreenedTickerDTO> insertAndUpdateScreenedTickers(List<ScreenedTickerDTO> ScreenedTickersDTOList);
    Optional<ScreenedTickerDTO> insertScreenedTicker(ScreenedTickerDTO screenedTickerDTO);
    Optional<ScreenedTickerDTO> updateScreenedTicker(ScreenedTickerDTO screenedTickerDTO);
    void deleteScreenedTickers();

}
