package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.UserOrder;
import com.tradingbot.dotty.models.dto.UserOrderDTO;
import com.tradingbot.dotty.repositories.UserOrdersRepository;
import com.tradingbot.dotty.service.UserOrdersService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;

@Slf4j
@Service
public class UserOrdersServiceImpl implements UserOrdersService {

    @Autowired
    private UserOrdersRepository userOrdersRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<UserOrderDTO> getUsersOrders() {
        log.trace(ENTITIES_READ_OPERATION, "UserOrder");
        return userOrdersRepository.findAll().stream().map(userOrder -> modelMapper.map(userOrder, UserOrderDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Long insertUserOrder(UserOrderDTO userOrderDTO) {
        log.trace(ENTITY_CREATE_OPERATION, userOrderDTO, "UserOrder");
        UserOrder userOrder = userOrdersRepository.save(modelMapper.map(userOrderDTO, UserOrder.class));
        return userOrder.getUserOrderId();
    }

    @Override
    public Long updateUserOrder(UserOrderDTO userOrderDTO) {
        log.trace(ENTITY_CREATE_OPERATION, userOrderDTO, "UserOrder");
        if(userOrderDTO.getUserOrderId() == null)
            throw new RuntimeException();
        Optional<UserOrder> userOrder = userOrdersRepository.findById(userOrderDTO.getUserOrderId());
        userOrder.ifPresent(userOrderDetails -> BeanUtils.copyProperties(userOrderDTO, userOrderDetails, "updatedAt"));
        UserOrder updatedUserOrder = userOrdersRepository.save(userOrder.get());
        return updatedUserOrder.getUserOrderId();
    }

    @Override
    public String deleteUserOrder() {
        return null;
    }
}
