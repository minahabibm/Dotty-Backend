package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.Users;
import com.tradingbot.dotty.models.dto.UsersDTO;
import com.tradingbot.dotty.repositories.UsersRepository;
import com.tradingbot.dotty.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;

@Slf4j
@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public List<UsersDTO> getUsers() {
        log.trace(ENTITIES_READ_OPERATION, "Users");
        return usersRepository.findAll().stream().map(user -> modelMapper.map(user, UsersDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Optional<UsersDTO> getUser(Long userId) {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, userId, "Users");
        return usersRepository.findById(userId).map(users -> modelMapper.map(users, UsersDTO.class));
    }

    @Override
    public Optional<UsersDTO> getUserByEmail(String email) {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, email, "Users");
        return usersRepository.findByEmailAddress(email).map(users -> modelMapper.map(users, UsersDTO.class));
    }

    @Override
    public Optional<UsersDTO> getUserByUserConfigurationId(Long userConfigurationId) {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, userConfigurationId, "Users");
        return usersRepository.findByUserConfiguration_UserConfigurationId(userConfigurationId).map(users -> modelMapper.map(users, UsersDTO.class));
    }


    @Override
    public Optional<UsersDTO> insertUser(UsersDTO usersDTO) {
        log.trace(ENTITY_CREATE_OPERATION, usersDTO, "Users");
        Users User = usersRepository.save(modelMapper.map(usersDTO, Users.class));
        return Optional.of(modelMapper.map(User, UsersDTO.class));
    }

    @Override
    public Optional<UsersDTO> updateUser(UsersDTO usersDTO) {
        log.trace(ENTITY_UPDATE_OPERATION, usersDTO.getUserId(), "Users");
        return usersRepository.findById(usersDTO.getUserId())
                .map(existingUser -> {
                    BeanUtils.copyProperties(usersDTO, existingUser, "updatedAt");
                    Users updatedUser = usersRepository.save(existingUser);
                    return modelMapper.map(updatedUser, UsersDTO.class);
                });
    }

    @Override
    public void deleteUser(Long userId) {
        log.trace(ENTITY_DELETE_OPERATION, userId, "Users");
        Users user = usersRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        usersRepository.delete(user);
    }

}
