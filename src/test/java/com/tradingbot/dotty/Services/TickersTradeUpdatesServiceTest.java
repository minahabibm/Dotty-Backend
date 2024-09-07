package com.tradingbot.dotty.Services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.TickersTradeUpdates;
import com.tradingbot.dotty.models.dto.TickersTradeUpdatesDTO;
import com.tradingbot.dotty.repositories.TickersTradeUpdatesRepository;
import com.tradingbot.dotty.serviceImpls.TickersTradeUpdatesServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class TickersTradeUpdatesServiceTest {

    @Mock
    private TickersTradeUpdatesRepository tickersTradeUpdatesRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TickersTradeUpdatesServiceImpl tickersTradeUpdatesService;


    @Test
    public void testGetTickersTradeUpdates() {
        // Arrange
        TickersTradeUpdates tradeUpdate = new TickersTradeUpdates();
        TickersTradeUpdatesDTO tradeUpdateDTO = new TickersTradeUpdatesDTO();

        when(tickersTradeUpdatesRepository.findAll()).thenReturn(Collections.singletonList(tradeUpdate));
        when(modelMapper.map(tradeUpdate, TickersTradeUpdatesDTO.class)).thenReturn(tradeUpdateDTO);

        // Act
        List<TickersTradeUpdatesDTO> result = tickersTradeUpdatesService.getTickersTradeUpdates();

        // Assert
        assertEquals(1, result.size());
        verify(tickersTradeUpdatesRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(tradeUpdate, TickersTradeUpdatesDTO.class);
    }

    @Test
    public void testGetSortedTickersTradeUpdates() {
        // Arrange
        TickersTradeUpdates tradeUpdate1 = new TickersTradeUpdates();
        ScreenedTicker screenedTicker1 = new ScreenedTicker();
        screenedTicker1.setSymbol("XYZ");
        screenedTicker1.setBeta(1.0f);
        screenedTicker1.setExchangeShortName("NYSE");
        tradeUpdate1.setScreenedTicker(screenedTicker1);

        TickersTradeUpdates tradeUpdate2 = new TickersTradeUpdates();
        ScreenedTicker screenedTicker2 = new ScreenedTicker();
        screenedTicker2.setSymbol("ABC");
        screenedTicker2.setBeta(0.9f);
        screenedTicker2.setExchangeShortName("NASDAQ");
        tradeUpdate2.setScreenedTicker(screenedTicker2);

        TickersTradeUpdatesDTO tradeUpdateDTO1 = new TickersTradeUpdatesDTO();
        TickersTradeUpdatesDTO tradeUpdateDTO2 = new TickersTradeUpdatesDTO();

        when(tickersTradeUpdatesRepository.findAll()).thenReturn(Arrays.asList(tradeUpdate1, tradeUpdate2));
        when(modelMapper.map(tradeUpdate1, TickersTradeUpdatesDTO.class)).thenReturn(tradeUpdateDTO1);
        when(modelMapper.map(tradeUpdate2, TickersTradeUpdatesDTO.class)).thenReturn(tradeUpdateDTO2);

        // Act
        List<TickersTradeUpdatesDTO> result = tickersTradeUpdatesService.getSortedTickersTradeUpdates(2);

        // Assert
        assertEquals(2, result.size());
        verify(tickersTradeUpdatesRepository, times(1)).findAll();
    }

    @Test
    public void testInsertTickersTradeUpdates() {
        // Arrange
        TickersTradeUpdates newTicker = new TickersTradeUpdates();
        newTicker.setSymbol("XYZ");
        TickersTradeUpdates savedTicker = new TickersTradeUpdates();
        TickersTradeUpdatesDTO savedTickerDTO = new TickersTradeUpdatesDTO();

        when(tickersTradeUpdatesRepository.findBySymbol(newTicker.getSymbol())).thenReturn(Optional.empty());
        when(tickersTradeUpdatesRepository.saveAll(anyList())).thenReturn(Collections.singletonList(savedTicker));
        when(modelMapper.map(savedTicker, TickersTradeUpdatesDTO.class)).thenReturn(savedTickerDTO);

        // Act
        List<TickersTradeUpdatesDTO> result = tickersTradeUpdatesService.insertTickersTradeUpdates(Collections.singletonList(newTicker));

        // Assert
        assertEquals(1, result.size());
        verify(tickersTradeUpdatesRepository, times(1)).saveAll(anyList());
        verify(modelMapper, times(1)).map(savedTicker, TickersTradeUpdatesDTO.class);
    }

}
