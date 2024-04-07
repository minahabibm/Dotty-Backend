package com.tradingbot.dotty.serviceImpls;

import static com.tradingbot.dotty.utils.LoggingConstants.*;
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
        log.trace(ENTITIES_READ_WITH_FILERS_OPERATION, "Orders", "Active");
        List<OrdersDTO> ordersDTOList = ordersRepository.findAllByActiveTrue().stream().map(order -> modelMapper.map(order, OrdersDTO.class)).collect(Collectors.toList());
        log.info("Number of Ticker with Active Orders, {}.", ordersDTOList.size());
        return ordersDTOList;
    }

    @Override
    public List<OrdersDTO> getOrdersByPositionTracker(Long positionTrackerId) {
        log.trace(ENTITIES_READ_WITH_FILERS_OPERATION, "Orders", "position Tracker Id: "+ positionTrackerId);
        return ordersRepository.findByPositionTracker_PositionTrackerIdOrderByCreatedAtAsc(positionTrackerId).stream().map(order -> modelMapper.map(order, OrdersDTO.class)).collect(Collectors.toList());
    }

    @Override
    public OrdersDTO getActiveTickerOrder(String symbol) {
        log.trace(ENTITIES_READ_WITH_FILERS_OPERATION, "Orders", "Symbol: "+ symbol +" and Active");
        Optional<Orders> order = ordersRepository.findBySymbolAndActiveTrue(symbol);
        OrdersDTO ordersDTO = null;
        if(order.isPresent())
            ordersDTO = modelMapper.map(order.get(), OrdersDTO.class);
        return ordersDTO;
    }

    @Override
    public List<Long> insertOrders(List<OrdersDTO> ordersDTOList) {
        return null;
    }

    @Override
    public Long insertOrder(OrdersDTO ordersDTO) {
        log.trace(ENTITY_CREATE_OPERATION, ordersDTO, "Orders");
        Orders order = ordersRepository.save(modelMapper.map(ordersDTO, Orders.class));
        return order.getOrderTickerId();
    }

    @Override
    public Long updateOrder(OrdersDTO ordersDTO) {
        log.trace(ENTITY_UPDATE_OPERATION, ordersDTO.getSymbol(), "Orders");
        if(ordersDTO.getOrderTickerId() == null)
            throw new RuntimeException();
        Optional<Orders> orders = ordersRepository.findById(ordersDTO.getOrderTickerId());
        orders.ifPresent(order -> BeanUtils.copyProperties(ordersDTO, order, "updatedAt"));
        Orders orderUpdated = ordersRepository.save(orders.get());
        return orderUpdated.getOrderTickerId();
    }

    @Override
    public String deleteOrder() {
        return null;
    }
}
