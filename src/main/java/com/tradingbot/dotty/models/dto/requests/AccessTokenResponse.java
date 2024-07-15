package com.tradingbot.dotty.models.dto.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenResponse {

    private String access_token;
    private String expires_in;
    private String scope;
    private String token_type;

}
