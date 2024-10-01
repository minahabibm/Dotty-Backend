package com.tradingbot.dotty.Services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Optional;

import com.tradingbot.dotty.models.UserOrder;
import com.tradingbot.dotty.models.dto.UserOrderDTO;
import com.tradingbot.dotty.repositories.UserOrdersRepository;
import com.tradingbot.dotty.serviceImpls.UserOrdersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
public class UserOrdersServiceTests {

    @Mock
    private UserOrdersRepository userOrdersRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserOrdersServiceImpl userOrdersService;

    private UserOrder userOrder;
    private UserOrderDTO userOrderDTO;

    @BeforeEach
    void setUp() {
        userOrder = new UserOrder();
        userOrder.setUserOrderId(1L);
        userOrderDTO = new UserOrderDTO();
        userOrderDTO.setUserOrderId(1L);

        // Set up common stubs
        lenient().when(modelMapper.map(userOrder, UserOrderDTO.class)).thenReturn(userOrderDTO);
        lenient().when(modelMapper.map(userOrderDTO, UserOrder.class)).thenReturn(userOrder);
    }

    @Test
    void testGetUsersOrders() {
        // Arrange
        when(userOrdersRepository.findAll()).thenReturn(Collections.singletonList(userOrder));

        // Act
        var result = userOrdersService.getUsersOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userOrderDTO.getUserOrderId(), result.get(0).getUserOrderId());
        verify(userOrdersRepository, times(1)).findAll();
    }

    @Test
    void testGetUserOrder() {
        // Arrange
        when(userOrdersRepository.findByAlpacaOrderId("123")).thenReturn(Optional.of(userOrder));

        // Act
        var result = userOrdersService.getUserOrder("123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userOrderDTO.getUserOrderId(), result.get().getUserOrderId());
        verify(userOrdersRepository, times(1)).findByAlpacaOrderId("123");
    }

    @Test
    void testInsertUserOrder() {
        // Arrange
        when(userOrdersRepository.save(userOrder)).thenReturn(userOrder);

        // Act
        var result = userOrdersService.insertUserOrder(userOrderDTO);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userOrderDTO.getUserOrderId(), result.get().getUserOrderId());
        verify(userOrdersRepository, times(1)).save(userOrder);
    }

    @Test
    void testUpdateUserOrder() {
        // Arrange
        UserOrderDTO updatedUserOrderDTO = new UserOrderDTO();
        updatedUserOrderDTO.setUserOrderId(1L);
        UserOrder existingUserOrder = new UserOrder();
        existingUserOrder.setUserOrderId(1L);

        when(userOrdersRepository.findById(1L)).thenReturn(Optional.of(existingUserOrder));
        when(userOrdersRepository.save(existingUserOrder)).thenReturn(existingUserOrder);

        // Act
        var result = userOrdersService.updateUserOrder(updatedUserOrderDTO);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(updatedUserOrderDTO.getUserOrderId(), result.get().getUserOrderId());
        verify(userOrdersRepository, times(1)).findById(1L);
        verify(userOrdersRepository, times(1)).save(existingUserOrder);
    }

    @Test
    void testUpdateUserOrderThrowsExceptionWhenIdIsNull() {
        // Arrange
        userOrderDTO.setUserOrderId(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userOrdersService.updateUserOrder(userOrderDTO));
        verify(userOrdersRepository, times(0)).findById(anyLong());
    }

    @Test
    void testDeleteUserOrder() {
        // Arrange
        when(userOrdersRepository.findById(1L)).thenReturn(Optional.of(userOrder));

        // Act
        userOrdersService.deleteUserOrder(1L);

        // Assert
        verify(userOrdersRepository, times(1)).delete(userOrder);
    }

    @Test
    void testDeleteUserOrderThrowsExceptionWhenNotFound() {
        // Arrange
        when(userOrdersRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userOrdersService.deleteUserOrder(1L));
        verify(userOrdersRepository, times(1)).findById(1L);
        verify(userOrdersRepository, times(0)).delete(any(UserOrder.class));
    }
}
