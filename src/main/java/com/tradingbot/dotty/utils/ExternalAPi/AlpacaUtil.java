package com.tradingbot.dotty.utils.ExternalAPi;

import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.UsersDTO;
import com.tradingbot.dotty.service.UserConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Slf4j
@Service
public class AlpacaUtil {

    @Autowired
    private WebClient webClientAlpaca;

    @Autowired
    private WebClient webClientAlpacaPaper;

}
