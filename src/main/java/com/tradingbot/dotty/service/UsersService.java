package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.UsersDTO;

import java.util.List;
import java.util.Optional;

public interface UsersService {

    List<UsersDTO> getUsers();
    Optional<UsersDTO> getUser(Long Id);
    Optional<UsersDTO> getUserByEmail(String email);
    Optional<UsersDTO> getUserByUserConfigurationId(Long userConfigurationId);
    Long insertUser(UsersDTO usersDTO);
    Long updateUser(UsersDTO usersDTO);
    String deleteUser(Long Id);

}
