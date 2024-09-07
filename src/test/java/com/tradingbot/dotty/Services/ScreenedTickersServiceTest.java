package com.tradingbot.dotty.Services;

import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;
import com.tradingbot.dotty.repositories.ScreenedTickersRepository;
import com.tradingbot.dotty.serviceImpls.ScreenedTickersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ScreenedTickersServiceTest {

    @Mock
    private ScreenedTickersRepository screenedTickersRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ScreenedTickersServiceImpl screenedTickersService;

    private ScreenedTicker screenedTicker1;
    private ScreenedTickerDTO screenedTickerDTO1;
    private ScreenedTicker screenedTicker2;
    private ScreenedTickerDTO screenedTickerDTO2;

    @BeforeEach
    void setUp() {
        // Mock objects setup
        screenedTicker1 = new ScreenedTicker();
        screenedTicker1.setSymbol("XYZ");
        screenedTicker1.setCreatedAt(LocalDateTime.now());

        screenedTickerDTO1 = new ScreenedTickerDTO();
        screenedTickerDTO1.setSymbol("XYZ");

        screenedTicker2 = new ScreenedTicker();
        screenedTicker2.setSymbol("ABC");
        screenedTicker2.setCreatedAt(LocalDateTime.now().minusDays(1));

        screenedTickerDTO2 = new ScreenedTickerDTO();
        screenedTickerDTO2.setSymbol("ABC");
    }

    @Test
    void testGetScreenedTickers() {
        // Arrange
        when(screenedTickersRepository.findAll()).thenReturn(Arrays.asList(screenedTicker1, screenedTicker2));
        when(modelMapper.map(screenedTicker1, ScreenedTickerDTO.class)).thenReturn(screenedTickerDTO1);
        when(modelMapper.map(screenedTicker2, ScreenedTickerDTO.class)).thenReturn(screenedTickerDTO2);

        // Act
        List<ScreenedTickerDTO> result = screenedTickersService.getScreenedTickers();

        // Assert
        assertEquals(2, result.size());
        assertEquals("XYZ", result.get(0).getSymbol());
        assertEquals("ABC", result.get(1).getSymbol());
        verify(screenedTickersRepository, times(1)).findAll();
    }

    @Test
    void testGetTodayScreenedTickers() {
        // Arrange
        screenedTicker1.setCreatedAt(LocalDateTime.now());
        screenedTicker2.setUpdatedAt(LocalDateTime.now());

        when(screenedTickersRepository.findAll()).thenReturn(Arrays.asList(screenedTicker1, screenedTicker2));
        when(modelMapper.map(screenedTicker1, ScreenedTickerDTO.class)).thenReturn(screenedTickerDTO1);
        when(modelMapper.map(screenedTicker2, ScreenedTickerDTO.class)).thenReturn(screenedTickerDTO2);

        // Act
        List<ScreenedTickerDTO> result = screenedTickersService.getTodayScreenedTickers();

        // Assert
        assertEquals(2, result.size());
        verify(screenedTickersRepository, times(1)).findAll();
    }

    @Test
    void testInsertAndUpdateScreenedTickers() {
        // Arrange
        when(screenedTickersRepository.findBySymbol("XYZ")).thenReturn(Optional.of(screenedTicker1));
        when(screenedTickersRepository.saveAll(anyList())).thenReturn(Arrays.asList(screenedTicker1, screenedTicker2));
        when(modelMapper.map(screenedTickerDTO1, ScreenedTicker.class)).thenReturn(screenedTicker1);
        when(modelMapper.map(screenedTickerDTO2, ScreenedTicker.class)).thenReturn(screenedTicker2);
        when(modelMapper.map(screenedTicker1, ScreenedTickerDTO.class)).thenReturn(screenedTickerDTO1);
        when(modelMapper.map(screenedTicker2, ScreenedTickerDTO.class)).thenReturn(screenedTickerDTO2);

        List<ScreenedTickerDTO> tickerDTOList = Arrays.asList(screenedTickerDTO1, screenedTickerDTO2);

        // Act
        List<ScreenedTickerDTO> result = screenedTickersService.insertAndUpdateScreenedTickers(tickerDTOList);

        // Assert
        assertEquals(2, result.size());
        verify(screenedTickersRepository, times(1)).findBySymbol("XYZ");
        verify(screenedTickersRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testInsertScreenedTicker() {
        // Arrange
        when(screenedTickersRepository.save(any(ScreenedTicker.class))).thenReturn(screenedTicker1);
        when(modelMapper.map(screenedTickerDTO1, ScreenedTicker.class)).thenReturn(screenedTicker1);
        when(modelMapper.map(screenedTicker1, ScreenedTickerDTO.class)).thenReturn(screenedTickerDTO1);

        // Act
        Optional<ScreenedTickerDTO> result = screenedTickersService.insertScreenedTicker(screenedTickerDTO1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("XYZ", result.get().getSymbol());
        verify(screenedTickersRepository, times(1)).save(any(ScreenedTicker.class));
    }

    @Test
    void testUpdateScreenedTicker() {
        // Arrange
        when(screenedTickersRepository.findBySymbol("XYZ")).thenReturn(Optional.of(screenedTicker1));
        when(screenedTickersRepository.save(any(ScreenedTicker.class))).thenReturn(screenedTicker1);
        when(modelMapper.map(screenedTicker1, ScreenedTickerDTO.class)).thenReturn(screenedTickerDTO1);

        // Act
        Optional<ScreenedTickerDTO> result = screenedTickersService.updateScreenedTicker(screenedTickerDTO1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("XYZ", result.get().getSymbol());
        verify(screenedTickersRepository, times(1)).findBySymbol("XYZ");
        verify(screenedTickersRepository, times(1)).save(any(ScreenedTicker.class));
    }

    @Test
    void testDeleteScreenedTickers() {
        // Act
        screenedTickersService.deleteScreenedTickers();

        // Assert
        verify(screenedTickersRepository, times(1)).deleteAll();
    }

}
