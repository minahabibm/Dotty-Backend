package com.tradingbot.dotty.Services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.tradingbot.dotty.models.PositionTracker;
import com.tradingbot.dotty.models.dto.PositionTrackerDTO;
import com.tradingbot.dotty.repositories.PositionTrackerRepository;
import com.tradingbot.dotty.serviceImpls.PositionTrackerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class PositionTrackerServiceTest {

    @InjectMocks
    private PositionTrackerServiceImpl positionTrackerService;

    @Mock
    private PositionTrackerRepository positionTrackerRepository;

    @Mock
    private ModelMapper modelMapper;

    private PositionTracker positionTracker;
    private PositionTrackerDTO positionTrackerDTO;

    @BeforeEach
    void setUp() {
        // Initialize PositionTracker object with some values
        positionTracker = new PositionTracker();
        positionTracker.setPositionTrackerId(1L);
        positionTracker.setSymbol("XYZ");
        positionTracker.setActive(true);
        positionTracker.setCreatedAt(LocalDateTime.now());
        positionTracker.setUpdatedAt(LocalDateTime.now());

        // Initialize PositionTrackerDTO object with corresponding values
        positionTrackerDTO = new PositionTrackerDTO();
        positionTrackerDTO.setPositionTrackerId(1L);
        positionTrackerDTO.setSymbol("XYZ");
        positionTrackerDTO.setActive(true);
        positionTrackerDTO.setCreatedAt(LocalDateTime.now());
        positionTrackerDTO.setUpdatedAt(LocalDateTime.now());

        // Lenient stubbing
        lenient().when(modelMapper.map(positionTracker, PositionTrackerDTO.class)).thenReturn(positionTrackerDTO);
        lenient().when(modelMapper.map(positionTrackerDTO, PositionTracker.class)).thenReturn(positionTracker);
    }

    @Test
    void testGetPositionTrackers() {
        when(positionTrackerRepository.findAll()).thenReturn(Collections.singletonList(positionTracker));

        List<PositionTrackerDTO> result = positionTrackerService.getPositionTrackers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(positionTrackerDTO.getPositionTrackerId(), result.get(0).getPositionTrackerId());
        verify(positionTrackerRepository, times(1)).findAll();
    }

    @Test
    void testGetPositionTrackerById() {
        when(positionTrackerRepository.findById(anyLong())).thenReturn(Optional.of(positionTracker));

        Optional<PositionTrackerDTO> result = positionTrackerService.getPositionTracker(1L);

        assertTrue(result.isPresent());
        assertEquals(positionTrackerDTO.getPositionTrackerId(), result.get().getPositionTrackerId());
        verify(positionTrackerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPositionTrackerBySymbol() {
        when(positionTrackerRepository.findBySymbolAndActiveTrue(anyString())).thenReturn(Optional.of(positionTracker));

        Optional<PositionTrackerDTO> result = positionTrackerService.getPositionTracker("XYZ");

        assertTrue(result.isPresent());
        assertEquals(positionTrackerDTO.getPositionTrackerId(), result.get().getPositionTrackerId());
        verify(positionTrackerRepository, times(1)).findBySymbolAndActiveTrue("XYZ");
    }

    @Test
    void testInsertPositionTracker() {
        when(positionTrackerRepository.save(any(PositionTracker.class))).thenReturn(positionTracker);

        Optional<PositionTrackerDTO> result = positionTrackerService.insertPositionTracker(positionTrackerDTO);

        assertTrue(result.isPresent());
        assertEquals(positionTrackerDTO.getPositionTrackerId(), result.get().getPositionTrackerId());
        verify(positionTrackerRepository, times(1)).save(positionTracker);
    }

    @Test
    void testUpdatePositionTracker() {
        when(positionTrackerRepository.findById(anyLong())).thenReturn(Optional.of(positionTracker));
        when(positionTrackerRepository.save(any(PositionTracker.class))).thenReturn(positionTracker);

        Optional<PositionTrackerDTO> result = positionTrackerService.updatePositionTracker(positionTrackerDTO);

        assertTrue(result.isPresent());
        assertEquals(positionTrackerDTO.getPositionTrackerId(), result.get().getPositionTrackerId());
        verify(positionTrackerRepository, times(1)).findById(positionTrackerDTO.getPositionTrackerId());
        verify(positionTrackerRepository, times(1)).save(positionTracker);
    }

    @Test
    void testDeletePositionTracker() {
        // Implement test for deletePositionTracker if functionality is provided
        // In this case, the method is empty so there's nothing to test.
    }
}
