package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.UserHolding;
import com.tradingbot.dotty.models.UserOrder;
import com.tradingbot.dotty.models.dto.UserHoldingDTO;
import com.tradingbot.dotty.models.dto.UserOrderDTO;
import com.tradingbot.dotty.repositories.UserHoldingRepository;
import com.tradingbot.dotty.repositories.UserOrdersRepository;
import com.tradingbot.dotty.service.UserHoldingService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.ENTITIES_READ_OPERATION;
import static com.tradingbot.dotty.utils.constants.LoggingConstants.ENTITY_CREATE_OPERATION;

@Slf4j
@Service
public class UserHoldingServiceImpl implements UserHoldingService {

    @Autowired
    private UserHoldingRepository userHoldingRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<UserHoldingDTO> getUsersHoldings() {
        log.trace(ENTITIES_READ_OPERATION, "UserHolding");
        return userHoldingRepository.findAll().stream().map(userHolding -> modelMapper.map(userHolding, UserHoldingDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Long insertUserHolding(UserHoldingDTO userHoldingDTO) {
        log.trace(ENTITY_CREATE_OPERATION, userHoldingDTO, "UserHolding");
        UserHolding userHolding = userHoldingRepository.save(modelMapper.map(userHoldingDTO, UserHolding.class));
        return userHolding.getUserHoldingId();
    }

    @Override
    public Long updateUserHolding(UserHoldingDTO userHoldingDTO) {
        log.trace(ENTITY_CREATE_OPERATION, userHoldingDTO, "UserHolding");
        if(userHoldingDTO.getUserHoldingId() == null)
            throw new RuntimeException();
        Optional<UserHolding> userHolding = userHoldingRepository.findById(userHoldingDTO.getUserHoldingId());
        userHolding.ifPresent(userHoldingDetails -> BeanUtils.copyProperties(userHoldingDTO, userHoldingDetails, "updatedAt"));
        UserHolding updatedUserHolding = userHoldingRepository.save(userHolding.get());
        return updatedUserHolding.getUserHoldingId();
    }

    @Override
    public String deleteUserHolding() {
        return null;
    }
}
