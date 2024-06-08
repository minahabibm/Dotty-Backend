package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.AuthUserTradingAccountAccessToken;

public interface UserTradingAccountAuth {

    AuthUserTradingAccountAccessToken OAuthFlowAppAuthorization(String code, String session, String redirect_uri);
    AuthUserTradingAccountAccessToken authorizeUserTradingAccountAccessToken(String code, String redirect_uri);
    AuthUserTradingAccountAccessToken authorizeUserTradingAccountRefreshToken(String RefreshToken);
}
