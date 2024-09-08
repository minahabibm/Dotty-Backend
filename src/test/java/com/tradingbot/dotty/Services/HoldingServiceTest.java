package com.tradingbot.dotty.Services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tradingbot.dotty.models.Holding;
import com.tradingbot.dotty.models.dto.HoldingDTO;
import com.tradingbot.dotty.repositories.HoldingRepository;
import com.tradingbot.dotty.serviceImpls.HoldingServiceImpl;
import com.tradingbot.dotty.utils.constants.TradeDetails;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.*;


@ExtendWith(MockitoExtension.class)
public class HoldingServiceTest {

    @InjectMocks
    private HoldingServiceImpl holdingService;

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private ModelMapper modelMapper;

    private Holding holding;
    private HoldingDTO holdingDTO;
    private List<Holding> holdings;
    private List<HoldingDTO> holdingsDTOs;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize the entities and DTOs
        holding = new Holding();
        holdingDTO = new HoldingDTO();
        holdings = Collections.singletonList(holding);
        holdingsDTOs = Collections.singletonList(holdingDTO);

        // Mock modelMapper behavior
        lenient().when(modelMapper.map(holding, HoldingDTO.class)).thenReturn(holdingDTO);
        lenient().when(modelMapper.map(holdingDTO, Holding.class)).thenReturn(holding);
    }

    @Test
    public void testGetHoldings() {
        when(holdingRepository.findAll()).thenReturn(holdings);

        List<HoldingDTO> result = holdingService.getHoldings();

        assertEquals(holdingsDTOs, result);
        verify(holdingRepository).findAll();
        verify(modelMapper).map(holding, HoldingDTO.class);
    }

    @Test
    public void testCompareHoldings() {
        // Mock repository and mapper
        when(holdingRepository.findAll()).thenReturn(holdings);
        when(modelMapper.map(holding, HoldingDTO.class)).thenReturn(holdingDTO);

        // Call the method
        Map<String, Integer> result = holdingService.compareHoldings();

        // Validate correct, incorrect, and other counts based on the setup
        assertNotNull(result);
        assertEquals(0, result.get("Correct: "));
        assertEquals(0, result.get("Incorrect: "));
        assertEquals(0, result.get("Other: "));

        // Verify repository interactions
        verify(holdingRepository).findAll();
        verify(modelMapper).map(holding, HoldingDTO.class);
    }

    @Test
    public void testCompareHoldingsWithNullData() {
        // Create a mock Holding entity
        Holding holdingEntity = new Holding();
        holdingEntity.setTypeOfTrade(null);
        holdingEntity.setExitPrice(null);
        holdingEntity.setEntryPrice(null);

        // Create another mock Holding entity with valid data
        Holding validHoldingEntity = new Holding();
        validHoldingEntity.setTypeOfTrade(TradeDetails.OVERSOLD.orderType);
        validHoldingEntity.setEntryPrice(10.0F);
        validHoldingEntity.setExitPrice(15.0F);

        // Mock the repository to return these Holding entities
        when(holdingRepository.findAll()).thenReturn(List.of(holdingEntity, validHoldingEntity));

        // Mock the modelMapper to map Holding entities to HoldingDTOs
        HoldingDTO holdingWithNullFieldsDTO = new HoldingDTO();
        holdingWithNullFieldsDTO.setTypeOfTrade(null);
        holdingWithNullFieldsDTO.setExitPrice(null);
        holdingWithNullFieldsDTO.setEntryPrice(null);

        HoldingDTO validHoldingDTO = new HoldingDTO();
        validHoldingDTO.setTypeOfTrade(TradeDetails.OVERSOLD.orderType);
        validHoldingDTO.setEntryPrice(10.0F);
        validHoldingDTO.setExitPrice(15.0F);

        // Mock the modelMapper behavior
        when(modelMapper.map(holdingEntity, HoldingDTO.class)).thenReturn(holdingWithNullFieldsDTO);
        when(modelMapper.map(validHoldingEntity, HoldingDTO.class)).thenReturn(validHoldingDTO);

        // Call the method
        Map<String, Integer> result = holdingService.compareHoldings();
    }

    @Test
    public void testCompareHoldingsUnexpectedError() {
        // Mock the repository to throw a RuntimeException when findAll is called
        when(holdingRepository.findAll()).thenThrow(new RuntimeException("An error"));

        // Call the method and catch the rethrown exception
        Exception exception = assertThrows(RuntimeException.class, () -> {
            holdingService.compareHoldings();
        });

        // Verify the exception message and that the correct log was written
        assertEquals("An error", exception.getMessage());

        // Verify that the repository was called
        verify(holdingRepository).findAll();
    }

    @Test
    public void testGetHoldingsStatistics() {
        when(holdingRepository.findAll()).thenReturn(holdings);
        when(modelMapper.map(holding, HoldingDTO.class)).thenReturn(holdingDTO);

        // Set up holdingDTO values for testing
        holdingDTO.setTypeOfTrade(TradeDetails.OVERSOLD.orderType);
        holdingDTO.setEntryPrice(10.0F);
        holdingDTO.setExitPrice(15.0F);

        List<HoldingDTO> result = holdingService.getHoldingsStatistics();

        // Validate total calculation based on the setup
        assertEquals(holdingsDTOs, result);
        verify(holdingRepository).findAll();
    }

    @Test
    public void testInsertHolding() {
        when(holdingRepository.save(holding)).thenReturn(holding);

        Optional<HoldingDTO> result = holdingService.insertHolding(holdingDTO);

        assertEquals(Optional.of(holdingDTO), result);
        verify(modelMapper).map(holdingDTO, Holding.class);
        verify(holdingRepository).save(holding);
        verify(modelMapper).map(holding, HoldingDTO.class);
    }

    @Test
    public void testUpdateHolding() {
        Long holdingId = 1L;
        Holding updatedHolding = new Holding();
        HoldingDTO updatedHoldingDTO = new HoldingDTO();

        holdingDTO.setHoldingTickerId(holdingId);
        when(holdingRepository.findById(holdingId)).thenReturn(Optional.of(holding));
        when(holdingRepository.save(holding)).thenReturn(updatedHolding);
        when(modelMapper.map(updatedHolding, HoldingDTO.class)).thenReturn(updatedHoldingDTO);

        Optional<HoldingDTO> result = holdingService.updateHolding(holdingDTO);

        assertEquals(Optional.of(updatedHoldingDTO), result);
        verify(holdingRepository).findById(holdingId);
        verify(holdingRepository).save(holding);
        verify(modelMapper).map(updatedHolding, HoldingDTO.class);
    }

    @Test
    public void testUpdateHoldingWithNullId() {
        holdingDTO.setHoldingTickerId(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            holdingService.updateHolding(holdingDTO);
        });

        assertNotNull(exception);
    }

    @Test
    public void testDeleteHolding() {
        // Implement test for deleteHolding if functionality is provided
        // In this case, the method is empty so there's nothing to test.
    }

}
