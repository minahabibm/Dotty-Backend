package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.UserHolding;
import com.tradingbot.dotty.models.dto.UserHoldingDTO;
import com.tradingbot.dotty.repositories.UserHoldingRepository;
import com.tradingbot.dotty.service.UserHoldingService;
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
    public Optional<UserHoldingDTO> insertUserHolding(UserHoldingDTO userHoldingDTO) {
        log.trace(ENTITY_CREATE_OPERATION, userHoldingDTO, "UserHolding");
        UserHolding userHolding = userHoldingRepository.save(modelMapper.map(userHoldingDTO, UserHolding.class));
        return Optional.of(modelMapper.map(userHolding, UserHoldingDTO.class));
    }

    @Override
    public Optional<UserHoldingDTO> updateUserHolding(UserHoldingDTO userHoldingDTO) {
        log.trace(ENTITY_CREATE_OPERATION, userHoldingDTO, "UserHolding");
        if(userHoldingDTO.getUserHoldingId() == null)
            throw new RuntimeException();
        return userHoldingRepository.findById(userHoldingDTO.getUserHoldingId())
                .map(existingUserHolding -> {
                    BeanUtils.copyProperties(userHoldingDTO, existingUserHolding, "updatedAt");
                    UserHolding updatedUserHolding = userHoldingRepository.save(existingUserHolding);
                    return modelMapper.map(updatedUserHolding, UserHoldingDTO.class);
                });
    }

    @Override
    public void deleteUserHolding(Long userHoldingId) {
        log.trace(ENTITY_DELETE_OPERATION, userHoldingId, "UserHolding");
        UserHolding userHolding = userHoldingRepository.findById(userHoldingId).orElseThrow(() -> new IllegalArgumentException("User Holding not found"));
        userHoldingRepository.delete(userHolding);
    }
}
