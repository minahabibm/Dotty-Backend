package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.dto.AuthRequestUserLogIn;
import com.tradingbot.dotty.service.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Override
    public String authorizeUserAfterAuthentication(AuthRequestUserLogIn authRequest) {
        String authorizationCode = authRequest.getCode();
        String idToken = authRequest.getIdToken();

        System.out.println(authorizationCode + " " +idToken);

//        Authentication auth =

        return null;
    }


}
