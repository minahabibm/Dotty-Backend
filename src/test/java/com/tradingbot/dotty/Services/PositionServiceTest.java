package com.tradingbot.dotty.Services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.tradingbot.dotty.models.Position;
import com.tradingbot.dotty.models.PositionTracker;
import com.tradingbot.dotty.models.dto.PositionDTO;
import com.tradingbot.dotty.models.dto.PositionTrackerDTO;
import com.tradingbot.dotty.repositories.PositionRepository;
import com.tradingbot.dotty.serviceImpls.PositionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class PositionServiceTest {

    @InjectMocks
    private PositionServiceImpl positionService;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private ModelMapper modelMapper;

    private Position position;
    private PositionDTO positionDTO;
    private PositionTracker positionTracker;
    private PositionTrackerDTO positionTrackerDTO;

    @BeforeEach
    void setUp() {
        // Initialize PositionTracker object with some values
        positionTracker = new PositionTracker();
        positionTracker.setPositionTrackerId(1L);
        positionTracker.setSymbol("XYZ");

        // Initialize PositionTrackerDTO object with corresponding values
        positionTrackerDTO = new PositionTrackerDTO();
        positionTrackerDTO.setPositionTrackerId(1L);
        positionTrackerDTO.setSymbol("XYZ");

        // Initialize Position object with some values
        position = new Position();
        position.setPositionId(1L);
        position.setSymbol("XYZ");
        position.setTai("TAI1");
        position.setTaiValue(12.34F);
        position.setIntervals(LocalDateTime.now());
        position.setOpen(150.50F);
        position.setHigh(155.00F);
        position.setLow(145.00F);
        position.setClose(152.00F);
        position.setVolume(1000L);
        position.setPositionTracker(positionTracker);
        position.setCreatedOn(LocalDateTime.now());

        // Initialize PositionDTO object with corresponding values
        positionDTO = new PositionDTO();
        positionDTO.setPositionId(1L);
        positionDTO.setSymbol("XYZ");
        positionDTO.setTai("TAI1");
        positionDTO.setTaiValue(12.34F);
        positionDTO.setIntervals(LocalDateTime.now());
        positionDTO.setOpen(150.50F);
        positionDTO.setHigh(155.00F);
        positionDTO.setLow(145.00F);
        positionDTO.setClose(152.00F);
        positionDTO.setVolume(1000L);
        positionDTO.setPositionTrackerDTO(positionTrackerDTO);
        positionDTO.setCreatedOn(LocalDateTime.now());

        // Lenient stubbing
        lenient().when(modelMapper.map(position, PositionDTO.class)).thenReturn(positionDTO);
        lenient().when(modelMapper.map(positionDTO, Position.class)).thenReturn(position);
    }

    @Test
    void testGetPositions() {
        when(positionRepository.findAll()).thenReturn(Collections.singletonList(position));

        List<PositionDTO> result = positionService.getPositions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(positionDTO.getPositionTrackerDTO().getPositionTrackerId(), result.get(0).getPositionTrackerDTO().getPositionTrackerId());
        verify(positionRepository, times(1)).findAll();
    }

    @Test
    void testGetSortedActiveTickerPositions() {
        when(positionRepository.findBySymbolAndPositionTracker_PositionTrackerIdOrderByIntervalsDesc(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(position));

        List<PositionDTO> result = positionService.getSortedActiveTickerPositions("XYZ", 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(positionDTO.getPositionTrackerDTO().getPositionTrackerId(), result.get(0).getPositionTrackerDTO().getPositionTrackerId());
        verify(positionRepository, times(1)).findBySymbolAndPositionTracker_PositionTrackerIdOrderByIntervalsDesc("XYZ", 1L);
    }

    @Test
    void testGetPositionBySymbolAndIntervals() {
        when(positionRepository.findBySymbolAndIntervals(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(position));

        Optional<PositionDTO> result = positionService.getPositionBySymbolAndIntervals("XYZ", LocalDateTime.now());

        assertTrue(result.isPresent());
        assertEquals(positionDTO.getPositionTrackerDTO().getPositionTrackerId(), result.get().getPositionTrackerDTO().getPositionTrackerId());
        verify(positionRepository, times(1)).findBySymbolAndIntervals(eq("XYZ"), any(LocalDateTime.class));
    }

    @Test
    void testInsertPositions() {
        when(positionRepository.saveAll(anyList())).thenReturn(Collections.singletonList(position));

        List<PositionDTO> result = positionService.insertPositions(Collections.singletonList(positionDTO));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(positionDTO.getPositionTrackerDTO().getPositionTrackerId(), result.get(0).getPositionTrackerDTO().getPositionTrackerId());
        verify(positionRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testInsertPosition() {
        when(positionRepository.save(any(Position.class))).thenReturn(position);

        Optional<PositionDTO> result = positionService.insertPosition(positionDTO);

        assertTrue(result.isPresent());
        assertEquals(positionDTO.getPositionTrackerDTO().getPositionTrackerId(), result.get().getPositionTrackerDTO().getPositionTrackerId());
        verify(positionRepository, times(1)).save(any(Position.class));
    }

    @Test
    void testUpdatePosition() {
        when(positionRepository.findById(anyLong())).thenReturn(Optional.of(position));
        when(positionRepository.save(any(Position.class))).thenReturn(position);

        Optional<PositionDTO> result = positionService.updatePosition(positionDTO);

        assertTrue(result.isPresent());
        assertEquals(positionDTO.getPositionTrackerDTO().getPositionTrackerId(), result.get().getPositionTrackerDTO().getPositionTrackerId());
        verify(positionRepository, times(1)).findById(positionDTO.getPositionId());
        verify(positionRepository, times(1)).save(position);
    }

    @Test
    void testDeletePosition() {
        // Implement test for deletePosition if functionality is provided
        // In this case, the method is empty so there's nothing to test.
    }
}