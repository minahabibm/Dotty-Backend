package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.AuthRequestUserLogIn;

public interface UserAccountService {

    String authorizeUserAfterAuthentication(AuthRequestUserLogIn authRequestUserLogIn);

}
