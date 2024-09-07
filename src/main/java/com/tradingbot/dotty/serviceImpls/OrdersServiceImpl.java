package com.tradingbot.dotty.serviceImpls;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;
import com.tradingbot.dotty.models.Orders;
import com.tradingbot.dotty.models.dto.OrdersDTO;
import com.tradingbot.dotty.repositories.OrdersRepository;
import com.tradingbot.dotty.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<OrdersDTO> getOrders() {
        log.trace(ENTITIES_READ_OPERATION, "Orders");
        return ordersRepository.findAll().stream().map(order -> modelMapper.map(order, OrdersDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<OrdersDTO> getActiveTickerOrders() {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, "Orders", "Active");
        return ordersRepository.findAllByActiveTrue().stream().map(order -> modelMapper.map(order, OrdersDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<OrdersDTO> getOrdersByPositionTracker(Long positionTrackerId) {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, "Orders", "position Tracker Id: "+ positionTrackerId);
        return ordersRepository.findByPositionTracker_PositionTrackerIdOrderByCreatedAtAsc(positionTrackerId).stream().map(order -> modelMapper.map(order, OrdersDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Optional<OrdersDTO> getActiveTickerOrder(String symbol) {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, "Orders", "Symbol: "+ symbol +" and Active");
        return ordersRepository.findBySymbolAndActiveTrue(symbol).map(order -> modelMapper.map(order, OrdersDTO.class));
    }

    @Override
    public Optional<OrdersDTO> insertOrder(OrdersDTO ordersDTO) {
        log.trace(ENTITY_CREATE_OPERATION, ordersDTO, "Orders");
        Orders order = ordersRepository.save(modelMapper.map(ordersDTO, Orders.class));
        return Optional.of(modelMapper.map(order, OrdersDTO.class));
    }

    @Override
    public Optional<OrdersDTO> updateOrder(OrdersDTO ordersDTO) {
        log.trace(ENTITY_UPDATE_OPERATION, ordersDTO.getSymbol(), "Orders");
        if(ordersDTO.getOrderTickerId() == null)
            throw new RuntimeException();
        return ordersRepository.findById(ordersDTO.getOrderTickerId())
                .map(existingOrder -> {
                    BeanUtils.copyProperties(ordersDTO, existingOrder, "updatedAt");
                    Orders updatedOrder = ordersRepository.save(existingOrder);
                    return modelMapper.map(updatedOrder, OrdersDTO.class);
                });
    }

    @Override
    public void deleteOrder() {

    }
}
