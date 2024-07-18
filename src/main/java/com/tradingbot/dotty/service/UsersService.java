package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.UsersDTO;
import com.tradingbot.dotty.models.dto.requests.UserTradingAccountAlpacaRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Optional;

public interface UsersService {

    List<UsersDTO> getUsers();
    Optional<UsersDTO> getUser(Long Id);
    Optional<UsersDTO> getUserByEmail(String email);
    Optional<UsersDTO> getUserByLoginUid(String email);
    Long insertUser(UsersDTO usersDTO);
    Long updateUser(UsersDTO usersDTO);
    String updateUserTradingAccountAlpaca(UserTradingAccountAlpacaRequest userTradingAccountAlpacaRequest, JwtAuthenticationToken jwtAuthenticationToken);
    String deleteUser(Long Id);

}
