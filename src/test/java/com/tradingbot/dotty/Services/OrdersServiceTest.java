package com.tradingbot.dotty.Services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tradingbot.dotty.models.Orders;
import com.tradingbot.dotty.models.dto.OrdersDTO;
import com.tradingbot.dotty.repositories.OrdersRepository;
import com.tradingbot.dotty.serviceImpls.OrdersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class OrdersServiceTest {

    @InjectMocks
    private OrdersServiceImpl ordersService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private ModelMapper modelMapper;

    private Orders order;
    private OrdersDTO orderDTO;
    private List<Orders> orders;
    private List<OrdersDTO> orderDTOs;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize the entities and DTOs
        order = new Orders();
        orderDTO = new OrdersDTO();
        orders = Collections.singletonList(order);
        orderDTOs = Collections.singletonList(orderDTO);

        // Lenient modelMapper behavior
        lenient().when(modelMapper.map(order, OrdersDTO.class)).thenReturn(orderDTO);
        lenient().when(modelMapper.map(orderDTO, Orders.class)).thenReturn(order);
    }

    @Test
    public void testGetOrders() {
        when(ordersRepository.findAll()).thenReturn(orders);

        List<OrdersDTO> result = ordersService.getOrders();

        assertEquals(orderDTOs, result);
        verify(ordersRepository).findAll();
        verify(modelMapper).map(order, OrdersDTO.class);
    }

    @Test
    public void testGetActiveTickerOrders() {
        when(ordersRepository.findAllByActiveTrue()).thenReturn(orders);

        List<OrdersDTO> result = ordersService.getActiveTickerOrders();

        assertEquals(orderDTOs, result);
        verify(ordersRepository).findAllByActiveTrue();
        verify(modelMapper).map(order, OrdersDTO.class);
    }

    @Test
    public void testGetOrdersByPositionTracker() {
        Long positionTrackerId = 1L;
        when(ordersRepository.findByPositionTracker_PositionTrackerIdOrderByCreatedAtAsc(positionTrackerId)).thenReturn(orders);

        List<OrdersDTO> result = ordersService.getOrdersByPositionTracker(positionTrackerId);

        assertEquals(orderDTOs, result);
        verify(ordersRepository).findByPositionTracker_PositionTrackerIdOrderByCreatedAtAsc(positionTrackerId);
        verify(modelMapper).map(order, OrdersDTO.class);
    }

    @Test
    public void testGetActiveTickerOrder() {
        String symbol = "XYZ";
        when(ordersRepository.findBySymbolAndActiveTrue(symbol)).thenReturn(Optional.of(order));

        Optional<OrdersDTO> result = ordersService.getActiveTickerOrder(symbol);

        assertEquals(Optional.of(orderDTO), result);
        verify(ordersRepository).findBySymbolAndActiveTrue(symbol);
        verify(modelMapper).map(order, OrdersDTO.class);
    }

    @Test
    public void testInsertOrder() {
        when(ordersRepository.save(order)).thenReturn(order);

        Optional<OrdersDTO> result = ordersService.insertOrder(orderDTO);

        assertEquals(Optional.of(orderDTO), result);
        verify(modelMapper).map(orderDTO, Orders.class);
        verify(ordersRepository).save(order);
        verify(modelMapper).map(order, OrdersDTO.class);
    }

    @Test
    public void testUpdateOrder() {
        Long orderId = 1L;
        Orders updatedOrder = new Orders();
        OrdersDTO updatedOrderDTO = new OrdersDTO();

        orderDTO.setOrderTickerId(orderId);
        when(ordersRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(ordersRepository.save(order)).thenReturn(updatedOrder);
        when(modelMapper.map(updatedOrder, OrdersDTO.class)).thenReturn(updatedOrderDTO);

        Optional<OrdersDTO> result = ordersService.updateOrder(orderDTO);

        assertEquals(Optional.of(updatedOrderDTO), result);
        verify(ordersRepository).findById(orderId);
        verify(ordersRepository).save(order);
        verify(modelMapper).map(updatedOrder, OrdersDTO.class);
    }

    @Test
    public void testUpdateOrderWithNullId() {
        orderDTO.setOrderTickerId(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ordersService.updateOrder(orderDTO);
        });

        assertNotNull(exception);
    }

    @Test
    public void testDeleteOrder() {
        // Implement test for deleteOrder if functionality is provided
        // In this case, the method is empty so there's nothing to test.
    }
}
