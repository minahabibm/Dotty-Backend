package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.AccessTokenAudAndJti;
import com.tradingbot.dotty.models.dto.AccessTokenResponse;
import com.tradingbot.dotty.models.dto.ScreenedTickersResponse;
import com.tradingbot.dotty.models.dto.TechnicalIndicatorResponse;

import java.time.LocalDateTime;

public interface ExternalApiService {

    ScreenedTickersResponse[] stockScreenerUpdateRetrieve();

    TechnicalIndicatorResponse technicalIndicatorRetrieve(String symbol, LocalDateTime localDateTime);

    AccessTokenResponse getMGMApiAccessToken();
    AccessTokenAudAndJti[] getRevokedAccessTokens(String mgmAccessToken);
    String revokeAccessToken(String mgmAccessToken, String jti);

}
