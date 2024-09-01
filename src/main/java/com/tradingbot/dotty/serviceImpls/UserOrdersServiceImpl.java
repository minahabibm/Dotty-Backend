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
    public Optional<UserOrderDTO> getUserOrder(String alpacaOrderId) {
        log.trace(ENTITIES_READ_OPERATION, "UserOrder");
        return userOrdersRepository.findByAlpacaOrderId(alpacaOrderId).map(userOrder -> modelMapper.map(userOrder, UserOrderDTO.class));
    }

    @Override
    public Optional<UserOrderDTO> insertUserOrder(UserOrderDTO userOrderDTO) {
        log.trace(ENTITY_CREATE_OPERATION, userOrderDTO, "UserOrder");
        UserOrder userOrder = userOrdersRepository.save(modelMapper.map(userOrderDTO, UserOrder.class));
        return Optional.of(modelMapper.map(userOrder, UserOrderDTO.class));
    }

    @Override
    public Optional<UserOrderDTO> updateUserOrder(UserOrderDTO userOrderDTO) {
        log.trace(ENTITY_CREATE_OPERATION, userOrderDTO, "UserOrder");
        if(userOrderDTO.getUserOrderId() == null)
            throw new RuntimeException();
        return userOrdersRepository.findById(userOrderDTO.getUserOrderId())
                .map(existingUserOrder -> {
                    BeanUtils.copyProperties(userOrderDTO, existingUserOrder, "updatedAt");
                    UserOrder updatedUserOrder = userOrdersRepository.save(existingUserOrder);
                    return modelMapper.map(updatedUserOrder, UserOrderDTO.class);
                });
    }

    @Override
    public void deleteUserOrder(Long userOrderId) {
        log.trace(ENTITY_DELETE_OPERATION, userOrderId, "UserOrder");
        UserOrder userOrder = userOrdersRepository.findById(userOrderId).orElseThrow(() -> new IllegalArgumentException("User order not found"));
        userOrdersRepository.delete(userOrder);
    }
}
