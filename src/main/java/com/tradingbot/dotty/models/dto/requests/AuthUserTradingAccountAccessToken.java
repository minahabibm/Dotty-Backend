package com.tradingbot.dotty.models.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserTradingAccountAccessToken {
    private Long expires_in;
    private String token_type;
    private String scope;
    private String refresh_token;
    private String access_token;
    private String id_token;
}
