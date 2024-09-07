package com.tradingbot.dotty.Services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.TickersTradeUpdates;
import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;
import com.tradingbot.dotty.models.dto.TickersTradeUpdatesDTO;
import com.tradingbot.dotty.repositories.TickersTradeUpdatesRepository;
import com.tradingbot.dotty.serviceImpls.TickersTradeUpdatesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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

    private TickersTradeUpdates tradeUpdate1;
    private TickersTradeUpdates tradeUpdate2;
    private TickersTradeUpdatesDTO tradeUpdateDTO1;
    private TickersTradeUpdatesDTO tradeUpdateDTO2;

    @BeforeEach
    void setUp() {
        // Initialize TickersTradeUpdates and DTO objects with some values
        tradeUpdate1 = new TickersTradeUpdates();
        tradeUpdate1.setScreenedTicker(new ScreenedTicker());
        tradeUpdate1.getScreenedTicker().setSymbol("XYZ");
        tradeUpdate1.getScreenedTicker().setBeta(1.0f);
        tradeUpdate1.getScreenedTicker().setExchangeShortName("NYSE");

        tradeUpdate2 = new TickersTradeUpdates();
        tradeUpdate2.setScreenedTicker(new ScreenedTicker());
        tradeUpdate2.getScreenedTicker().setSymbol("ABC");
        tradeUpdate2.getScreenedTicker().setBeta(0.9f);
        tradeUpdate2.getScreenedTicker().setExchangeShortName("NASDAQ");

        tradeUpdateDTO1 = new TickersTradeUpdatesDTO();
        tradeUpdateDTO1.setScreenedTickerDTO(new ScreenedTickerDTO());
        tradeUpdateDTO1.getScreenedTickerDTO().setSymbol("XYZ");

        tradeUpdateDTO2 = new TickersTradeUpdatesDTO();
        tradeUpdateDTO2.setScreenedTickerDTO(new ScreenedTickerDTO());
        tradeUpdateDTO2.getScreenedTickerDTO().setSymbol("ABC");

        // Lenient stubbing for modelMapper
        lenient().when(modelMapper.map(tradeUpdate1, TickersTradeUpdatesDTO.class)).thenReturn(tradeUpdateDTO1);
        lenient().when(modelMapper.map(tradeUpdate2, TickersTradeUpdatesDTO.class)).thenReturn(tradeUpdateDTO2);
        lenient().when(modelMapper.map(tradeUpdateDTO1, TickersTradeUpdates.class)).thenReturn(tradeUpdate1);
        lenient().when(modelMapper.map(tradeUpdateDTO2, TickersTradeUpdates.class)).thenReturn(tradeUpdate2);
    }

    @Test
    void testGetTickersTradeUpdates() {
        // Arrange
        when(tickersTradeUpdatesRepository.findAll()).thenReturn(Collections.singletonList(tradeUpdate1));

        // Act
        List<TickersTradeUpdatesDTO> result = tickersTradeUpdatesService.getTickersTradeUpdates();

        // Assert
        assertEquals(1, result.size());
        verify(tickersTradeUpdatesRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(tradeUpdate1, TickersTradeUpdatesDTO.class);
    }

    @Test
    void testGetSortedTickersTradeUpdates() {
        // Arrange
        when(tickersTradeUpdatesRepository.findAll()).thenReturn(Arrays.asList(tradeUpdate1, tradeUpdate2));

        // Act
        List<TickersTradeUpdatesDTO> result = tickersTradeUpdatesService.getSortedTickersTradeUpdates(2);

        // Assert
        assertEquals(2, result.size());
        verify(tickersTradeUpdatesRepository, times(1)).findAll();
    }

    @Test
    void testInsertTickersTradeUpdates() {
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
