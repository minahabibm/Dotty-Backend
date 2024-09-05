package com.tradingbot.dotty.Services;


import com.tradingbot.dotty.models.UserHolding;
import com.tradingbot.dotty.models.dto.UserHoldingDTO;
import com.tradingbot.dotty.repositories.UserHoldingRepository;
import com.tradingbot.dotty.serviceImpls.UserHoldingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserHoldingServiceTest {

    @InjectMocks
    private UserHoldingServiceImpl userHoldingService;

    @Mock
    private UserHoldingRepository userHoldingRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void testGetUsersHoldings() {
        // Arrange
        UserHolding userHolding = new UserHolding();
        UserHoldingDTO userHoldingDTO = new UserHoldingDTO();
        List<UserHolding> userHoldings = Arrays.asList(userHolding);
        List<UserHoldingDTO> userHoldingDTOs = Arrays.asList(userHoldingDTO);

        when(userHoldingRepository.findAll()).thenReturn(userHoldings);
        when(modelMapper.map(userHolding, UserHoldingDTO.class)).thenReturn(userHoldingDTO);

        // Act
        List<UserHoldingDTO> result = userHoldingService.getUsersHoldings();

        // Assert
        assertEquals(userHoldingDTOs, result);
        verify(userHoldingRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(userHolding, UserHoldingDTO.class);
    }

    @Test
    void testInsertUserHolding() {
        // Arrange
        UserHoldingDTO userHoldingDTO = new UserHoldingDTO();
        UserHolding userHolding = new UserHolding();
        when(modelMapper.map(userHoldingDTO, UserHolding.class)).thenReturn(userHolding);
        when(userHoldingRepository.save(userHolding)).thenReturn(userHolding);
        when(modelMapper.map(userHolding, UserHoldingDTO.class)).thenReturn(userHoldingDTO);

        // Act
        Optional<UserHoldingDTO> result = userHoldingService.insertUserHolding(userHoldingDTO);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userHoldingDTO, result.get());
        verify(modelMapper, times(1)).map(userHoldingDTO, UserHolding.class);
        verify(userHoldingRepository, times(1)).save(userHolding);
        verify(modelMapper, times(1)).map(userHolding, UserHoldingDTO.class);
    }

    @Test
    void testUpdateUserHolding() {
        // Arrange
        UserHoldingDTO userHoldingDTO = new UserHoldingDTO();
        UserHolding existingUserHolding = new UserHolding();
        UserHolding updatedUserHolding = new UserHolding();
        userHoldingDTO.setUserHoldingId(1L);
        when(userHoldingRepository.findById(1L)).thenReturn(Optional.of(existingUserHolding));
        when(userHoldingRepository.save(existingUserHolding)).thenReturn(updatedUserHolding);
        when(modelMapper.map(updatedUserHolding, UserHoldingDTO.class)).thenReturn(userHoldingDTO);

        // Act
        Optional<UserHoldingDTO> result = userHoldingService.updateUserHolding(userHoldingDTO);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userHoldingDTO, result.get());
        verify(userHoldingRepository, times(1)).findById(1L);
        verify(userHoldingRepository, times(1)).save(existingUserHolding);
        verify(modelMapper, times(1)).map(updatedUserHolding, UserHoldingDTO.class);
    }

    @Test
    void testUpdateUserHolding_ThrowsExceptionWhenIdIsNull() {
        // Arrange
        UserHoldingDTO userHoldingDTO = new UserHoldingDTO();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userHoldingService.updateUserHolding(userHoldingDTO));
        verify(userHoldingRepository, times(0)).findById(anyLong());
    }

    @Test
    void testDeleteUserHolding() {
        // Arrange
        Long userHoldingId = 1L;
        UserHolding userHolding = new UserHolding();
        when(userHoldingRepository.findById(userHoldingId)).thenReturn(Optional.of(userHolding));

        // Act
        userHoldingService.deleteUserHolding(userHoldingId);

        // Assert
        verify(userHoldingRepository, times(1)).findById(userHoldingId);
        verify(userHoldingRepository, times(1)).delete(userHolding);
    }

    @Test
    void testDeleteUserHolding_ThrowsExceptionWhenNotFound() {
        // Arrange
        Long userHoldingId = 1L;
        when(userHoldingRepository.findById(userHoldingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userHoldingService.deleteUserHolding(userHoldingId));
        verify(userHoldingRepository, times(1)).findById(userHoldingId);
        verify(userHoldingRepository, times(0)).delete(any(UserHolding.class));
    }

}
