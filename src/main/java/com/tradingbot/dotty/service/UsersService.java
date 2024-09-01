package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.UsersDTO;

import java.util.List;
import java.util.Optional;

public interface UsersService {

    List<UsersDTO> getUsers();
    Optional<UsersDTO> getUser(Long userId);
    Optional<UsersDTO> getUserByEmail(String email);
    Optional<UsersDTO> getUserByUserConfigurationId(Long userConfigurationId);
    Optional<UsersDTO> insertUser(UsersDTO usersDTO);
    Optional<UsersDTO> updateUser(UsersDTO usersDTO);
    void deleteUser(Long userId);

}
