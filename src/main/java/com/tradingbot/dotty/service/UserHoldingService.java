package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.UserHolding;
import com.tradingbot.dotty.models.dto.UserHoldingDTO;
import com.tradingbot.dotty.models.dto.UserOrderDTO;

import java.util.List;

public interface UserHoldingService {

    List<UserHoldingDTO> getUsersHoldings();
    Long insertUserHolding(UserHoldingDTO userHoldingDTO);
    Long updateUserHolding(UserHoldingDTO userHoldingDTO);
    String deleteUserHolding();

}
