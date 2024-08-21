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
    public Optional<UsersDTO> getUser(Long id) {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, id, "Users");
        Optional<Users> user = usersRepository.findById(id);
        return user.map(users -> modelMapper.map(users, UsersDTO.class));
    }

    @Override
    public Optional<UsersDTO> getUserByEmail(String email) {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, email, "Users");
        Optional<Users> user = usersRepository.findByEmailAddress(email);
        return user.map(users -> modelMapper.map(users, UsersDTO.class));
    }

    @Override
    public Optional<UsersDTO> getUserByLoginUid(String loginUid) {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, loginUid, "Users");
        Optional<Users> user = usersRepository.findByLoginUid(loginUid);
        return user.map(users -> modelMapper.map(users, UsersDTO.class));
    }

    @Override
    public Long insertUser(UsersDTO usersDTO) {
        log.trace(ENTITY_CREATE_OPERATION, usersDTO, "Users");
        Users User = usersRepository.save(modelMapper.map(usersDTO, Users.class));
        return User.getUserId();
    }

    @Override
    public Long updateUser(UsersDTO usersDTO) {
        log.trace(ENTITY_UPDATE_OPERATION, usersDTO.getUserId(), "Users");
        Optional<Users> user = usersRepository.findById(usersDTO.getUserId());
        user.ifPresent(userDAO -> BeanUtils.copyProperties(usersDTO, userDAO, "updatedAt"));
        Users users = usersRepository.save(user.get());
        return users.getUserId();
    }

    @Override
    public String deleteUser(Long Id) {
        return null;
    }

}
