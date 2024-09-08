package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.UserHoldingDTO;

import java.util.List;
import java.util.Optional;

public interface UserHoldingService {

    List<UserHoldingDTO> getUsersHoldings();
    Optional<UserHoldingDTO> insertUserHolding(UserHoldingDTO userHoldingDTO);
    Optional<UserHoldingDTO> updateUserHolding(UserHoldingDTO userHoldingDTO);
    void deleteUserHolding(Long userHoldingId);

}
