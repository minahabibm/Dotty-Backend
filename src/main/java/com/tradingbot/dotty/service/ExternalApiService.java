package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.*;

import java.time.LocalDateTime;

public interface ExternalApiService {

    ScreenedTickersResponse[] stockScreenerUpdateRetrieve();

    TechnicalIndicatorResponse technicalIndicatorRetrieve(String symbol, LocalDateTime localDateTime);

    AccessTokenResponse getMGMApiAccessToken();
    AccessTokenAudAndJti[] getRevokedAccessTokens(String mgmAccessToken);
    String revokeAccessToken(String mgmAccessToken, String jti);

    AuthUserTradingAccountAccessToken authorizeUserTradingAccountAccessToken(String code, String session);
    AuthUserTradingAccountAccessToken authorizeUserTradingAccountRefreshToken(String RefreshToken);

}
