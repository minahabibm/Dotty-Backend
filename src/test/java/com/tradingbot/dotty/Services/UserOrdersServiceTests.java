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
    }

    @Test
    void testGetUsersOrders() {
        when(userOrdersRepository.findAll()).thenReturn(Collections.singletonList(userOrder));
        when(modelMapper.map(userOrder, UserOrderDTO.class)).thenReturn(userOrderDTO);

        var result = userOrdersService.getUsersOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userOrderDTO.getUserOrderId(), result.get(0).getUserOrderId());
        verify(userOrdersRepository).findAll();
    }

    @Test
    void testGetUserOrder() {
        when(userOrdersRepository.findByAlpacaOrderId("123")).thenReturn(Optional.of(userOrder));
        when(modelMapper.map(userOrder, UserOrderDTO.class)).thenReturn(userOrderDTO);

        var result = userOrdersService.getUserOrder("123");

        assertTrue(result.isPresent());
        assertEquals(userOrderDTO.getUserOrderId(), result.get().getUserOrderId());
        verify(userOrdersRepository).findByAlpacaOrderId("123");
    }

    @Test
    void testInsertUserOrder() {
        when(modelMapper.map(userOrderDTO, UserOrder.class)).thenReturn(userOrder);
        when(userOrdersRepository.save(userOrder)).thenReturn(userOrder);
        when(modelMapper.map(userOrder, UserOrderDTO.class)).thenReturn(userOrderDTO);

        var result = userOrdersService.insertUserOrder(userOrderDTO);

        assertTrue(result.isPresent());
        assertEquals(userOrderDTO.getUserOrderId(), result.get().getUserOrderId());
        verify(userOrdersRepository).save(userOrder);
    }

    @Test
    void testUpdateUserOrder() {
        UserOrderDTO userOrderDTO = new UserOrderDTO();
        userOrderDTO.setUserOrderId(1L);
        UserOrder existingUserOrder = new UserOrder();
        existingUserOrder.setUserOrderId(1L);

        // Setting up mocks
        when(userOrdersRepository.findById(1L)).thenReturn(Optional.of(existingUserOrder));
        when(userOrdersRepository.save(any(UserOrder.class))).thenReturn(existingUserOrder);
        when(modelMapper.map(existingUserOrder, UserOrderDTO.class)).thenReturn(userOrderDTO);

        // Call the service method
        var result = userOrdersService.updateUserOrder(userOrderDTO);

        // Verify results
        assertTrue(result.isPresent());
        assertEquals(userOrderDTO.getUserOrderId(), result.get().getUserOrderId());

        // Verify interactions
        verify(userOrdersRepository).findById(1L);
        verify(userOrdersRepository).save(existingUserOrder);
        verify(modelMapper).map(existingUserOrder, UserOrderDTO.class);

    }

    @Test
    void testUpdateUserOrderThrowsExceptionWhenIdIsNull() {
        userOrderDTO.setUserOrderId(null);

        assertThrows(RuntimeException.class, () -> userOrdersService.updateUserOrder(userOrderDTO));
    }

    @Test
    void testDeleteUserOrder() {
        when(userOrdersRepository.findById(1L)).thenReturn(Optional.of(userOrder));

        userOrdersService.deleteUserOrder(1L);

        verify(userOrdersRepository).delete(userOrder);
    }

    @Test
    void testDeleteUserOrderThrowsExceptionWhenNotFound() {
        when(userOrdersRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userOrdersService.deleteUserOrder(1L));
    }

}
