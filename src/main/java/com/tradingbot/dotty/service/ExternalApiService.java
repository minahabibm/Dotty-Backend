package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.*;

import java.time.LocalDateTime;

public interface ExternalApiService {

    ScreenedTickersResponse[] stockScreenerUpdateRetrieve();

    TechnicalIndicatorResponse technicalIndicatorRetrieve(String symbol, LocalDateTime localDateTime);

    AccessTokenResponse getMGMApiAccessToken();
    AccessTokenAudAndJti[] getRevokedAccessTokens(String mgmAccessToken);
    String revokeAccessToken(String mgmAccessToken, String jti);

    AuthUserTradingAccountAccessToken OAuthFlowAppAuthorization(String code, String session, String redirect_uri);
    AuthUserTradingAccountAccessToken authorizeUserTradingAccountAccessToken(String code, String redirect_uri);
    AuthUserTradingAccountAccessToken authorizeUserTradingAccountRefreshToken(String RefreshToken);

}
