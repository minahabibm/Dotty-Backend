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

import java.time.LocalDateTime;
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
        holding.setHoldingTickerId(1L);
        holding.setSymbol("AAPL");
        holding.setTypeOfTrade(TradeDetails.OVERSOLD.orderType);
        holding.setEntryPrice(100.0F);
        holding.setExitPrice(110.0F);
        holding.setQuantity(10L);
        holding.setStartAt(LocalDateTime.now().minusDays(5));
        holding.setEndAt(LocalDateTime.now());

        holdingDTO = new HoldingDTO();
        holdingDTO.setHoldingTickerId(1L);
        holdingDTO.setSymbol("AAPL");
        holdingDTO.setTypeOfTrade(TradeDetails.OVERSOLD.orderType);
        holdingDTO.setEntryPrice(100.0F);
        holdingDTO.setExitPrice(110.0F);
        holdingDTO.setQuantity(10L);
        holdingDTO.setStartAt(LocalDateTime.now().minusDays(5));
        holdingDTO.setEndAt(LocalDateTime.now());

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
    public void testInsertHolding() {
        // Mock repository response
        when(holdingRepository.save(holding)).thenReturn(holding);

        // Call the service method
        Optional<HoldingDTO> result = holdingService.insertHolding(holdingDTO);

        // Verify the result and interactions
        assertTrue(result.isPresent());
        assertEquals(holdingDTO, result.get());
        verify(modelMapper).map(holdingDTO, Holding.class);
        verify(holdingRepository).save(holding);
        verify(modelMapper).map(holding, HoldingDTO.class);
    }

    @Test
    public void testUpdateHolding() {
        Long holdingId = 1L;
        Holding updatedHolding = new Holding();
        HoldingDTO updatedHoldingDTO = new HoldingDTO();

        // Set up the holding ID for the DTO
        holdingDTO.setHoldingTickerId(holdingId);

        // Mock repository and mapper behavior
        when(holdingRepository.findById(holdingId)).thenReturn(Optional.of(holding));
        when(holdingRepository.save(holding)).thenReturn(updatedHolding);
        when(modelMapper.map(updatedHolding, HoldingDTO.class)).thenReturn(updatedHoldingDTO);

        // Call the service method
        Optional<HoldingDTO> result = holdingService.updateHolding(holdingDTO);

        // Verify the result and interactions
        assertTrue(result.isPresent());
        assertEquals(updatedHoldingDTO, result.get());
        verify(holdingRepository).findById(holdingId);
        verify(holdingRepository).save(holding);
        verify(modelMapper).map(updatedHolding, HoldingDTO.class);
    }

    @Test
    public void testUpdateHoldingWithNullId() {
        holdingDTO.setHoldingTickerId(null);

        // Call the service method and expect an exception
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

    @Test
    public void testCompareHoldings() {
        // Mock repository and mapper
        when(holdingRepository.findAll()).thenReturn(holdings);
        when(modelMapper.map(holding, HoldingDTO.class)).thenReturn(holdingDTO);

        // Call the method
        Map<String, Integer> result = holdingService.compareHoldings();

        // Validate correct, incorrect, and other counts based on the setup
        assertNotNull(result);
        assertEquals(1, result.get("Correct: "));
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

//    @Test
//    public void testGetHoldingsStatisticsValidData() {
//        // Prepare valid data
//        HoldingDTO holding1 = new HoldingDTO();
//        holding1.setTypeOfTrade(TradeDetails.OVERSOLD.orderType);
//        holding1.setEntryPrice(100.0F);
//        holding1.setExitPrice(110.0F);
//        holding1.setStartAt(LocalDateTime.now().minusDays(2));
//        holding1.setEndAt(LocalDateTime.now());
//
//        HoldingDTO holding2 = new HoldingDTO();
//        holding2.setTypeOfTrade(TradeDetails.OVERBOUGHT.orderType);
//        holding2.setEntryPrice(150.0F);
//        holding2.setExitPrice(140.0F);
//        holding2.setStartAt(LocalDateTime.now().minusDays(3));
//        holding2.setEndAt(LocalDateTime.now());
//
//        holdingsDTOs.add(holding1);
//        holdingsDTOs.add(holding2);
//
//        // Mock the getHoldings method to return the prepared data
//        when(holdingService.getHoldings()).thenReturn(holdingsDTOs);
//
//        // Call the method
//        Map<String, Number> statistics = holdingService.getHoldingsStatistics();
//
//        // Verify the output
//        assertNotNull(statistics);
//        assertEquals(2, statistics.get("Positions "));
//        assertEquals(200.0, statistics.get("Total "));
//        assertEquals(1, statistics.get("long positions "));
//        assertEquals(100.0, statistics.get("Total Long Positions "));
//        assertEquals(1, statistics.get("Short positions "));
//        assertEquals(100.0, statistics.get("Total short positions "));
//        assertEquals(3, statistics.get("Longest period (in days)"));
//        assertEquals(0, statistics.get("Shortest period (in minutes)")); // This is a basic case; adjust based on the expected result.
//        assertEquals(2, statistics.get("Number of swing trades"));
//        assertEquals(0, statistics.get("Number of Intra trades"));
//    }
//
//    @Test
//    public void testGetHoldingsStatisticsWithNullValues() {
//        // Prepare data with null fields
//        HoldingDTO holdingWithNullFields = new HoldingDTO();
//        holdingWithNullFields.setTypeOfTrade(TradeDetails.OVERSOLD.orderType);
//        holdingWithNullFields.setEntryPrice(null); // Null entry price
//        holdingWithNullFields.setExitPrice(110.0F);
//        holdingWithNullFields.setStartAt(LocalDateTime.now().minusDays(2));
//        holdingWithNullFields.setEndAt(LocalDateTime.now());
//
//        holdingsDTOs.add(holdingWithNullFields);
//
//        // Mock the getHoldings method to return the prepared data
//        when(holdingService.getHoldings()).thenReturn(holdingsDTOs);
//
//        // Call the method
//        Map<String, Number> statistics = holdingService.getHoldingsStatistics();
//
//        // Verify that the holding with null entry price is skipped
//        assertNotNull(statistics);
//        assertEquals(0, statistics.get("Positions "));
//        assertEquals(0.0, statistics.get("Total "));
//    }
//
//    @Test
//    public void testGetHoldingsStatisticsWithNegativeDuration() {
//        // Prepare data with negative duration (invalid)
//        HoldingDTO holdingWithNegativeDuration = new HoldingDTO();
//        holdingWithNegativeDuration.setTypeOfTrade(TradeDetails.OVERBOUGHT.orderType);
//        holdingWithNegativeDuration.setEntryPrice(150.0F);
//        holdingWithNegativeDuration.setExitPrice(140.0F);
//        holdingWithNegativeDuration.setStartAt(LocalDateTime.now().plusDays(2)); // Start date in the future
//        holdingWithNegativeDuration.setEndAt(LocalDateTime.now());
//
//        holdingsDTOs.add(holdingWithNegativeDuration);
//
//        // Mock the getHoldings method to return the prepared data
//        when(holdingService.getHoldings()).thenReturn(holdingsDTOs);
//
//        // Call the method
//        Map<String, Number> statistics = holdingService.getHoldingsStatistics();
//
//        // Verify that the holding with negative duration is skipped
//        assertNotNull(statistics);
//        assertEquals(0, statistics.get("Positions "));
//        assertEquals(0.0, statistics.get("Total "));
//    }

    @Test
    public void testGetHoldingsStatisticsEmptyHoldings() {
        // Mock the getHoldings method to return an empty list
        when(holdingService.getHoldings()).thenReturn(Collections.emptyList());

        // Call the method
        Map<String, Number> statistics = holdingService.getHoldingsStatistics();

        // Verify that the statistics are empty
        assertNotNull(statistics);
        assertEquals(0, statistics.get("Positions "));
        assertEquals(0.0, statistics.get("Total "));
    }

    @Test
    public void testGetHoldingsStatisticsUnexpectedError() {
        // Mock the getHoldings method to throw an exception
        when(holdingService.getHoldings()).thenThrow(new RuntimeException("Unexpected error"));

        // Call the method and expect an exception
        Exception exception = assertThrows(RuntimeException.class, () -> {
            holdingService.getHoldingsStatistics();
        });

        // Verify the exception message
        assertEquals("Unexpected error", exception.getMessage());
    }

}
