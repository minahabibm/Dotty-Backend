package com.tradingbot.dotty.models.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestUserLogIn {

    private String code;
    private String idToken;

}
