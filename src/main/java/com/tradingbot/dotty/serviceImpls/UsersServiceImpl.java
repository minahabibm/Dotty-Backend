package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.Users;
import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.UsersDTO;
import com.tradingbot.dotty.models.dto.requests.UserTradingAccountAlpacaRequest;
import com.tradingbot.dotty.repositories.UsersRepository;
import com.tradingbot.dotty.service.UserConfigurationService;
import com.tradingbot.dotty.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private UserConfigurationService userConfigurationService;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public List<UsersDTO> getUsers() {
        log.trace(ENTITIES_READ_OPERATION, "Users");
        return usersRepository.findAll().stream().map(user -> modelMapper.map(user, UsersDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Optional<UsersDTO> getUser(Long id) {
        log.trace(ENTITIES_READ_WITH_FILERS_OPERATION, id, "Users");
        Optional user = usersRepository.findById(id);
        if (user.isPresent()) return Optional.of(modelMapper.map(user.get(), UsersDTO.class));
        return Optional.empty();
    }

    @Override
    public Optional<UsersDTO> getUserByEmail(String email) {
        log.trace(ENTITIES_READ_WITH_FILERS_OPERATION, email, "Users");
        Optional user = usersRepository.findByEmailAddress(email);
        if (user.isPresent()) return Optional.of(modelMapper.map(user.get(), UsersDTO.class));
        return Optional.empty();
    }

    @Override
    public Optional<UsersDTO> getUserByLoginUid(String loginUid) {
        log.trace(ENTITIES_READ_WITH_FILERS_OPERATION, loginUid, "Users");
        Optional user = usersRepository.findByLoginUid(loginUid);
        if (user.isPresent())
            return Optional.of(modelMapper.map(user.get(), UsersDTO.class));
        return Optional.empty();
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
    public String updateUserTradingAccountAlpaca(UserTradingAccountAlpacaRequest userTradingAccountAlpacaRequest, JwtAuthenticationToken jwtAuthenticationToken) {
        System.out.println(userTradingAccountAlpacaRequest.getKey() + " " + userTradingAccountAlpacaRequest.getSecret());

        if (userTradingAccountAlpacaRequest.getKey() == null || userTradingAccountAlpacaRequest.getSecret() == null || userTradingAccountAlpacaRequest.getKey().isEmpty() || userTradingAccountAlpacaRequest.getSecret().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        getUserByLoginUid(jwtAuthenticationToken.getName()).ifPresentOrElse(userDTO -> {
            UserConfigurationDTO userConfigurationDTO = userDTO.getUserConfigurationDTO();
            userConfigurationDTO.setAlpacaApiKey(userTradingAccountAlpacaRequest.getKey());
            userConfigurationDTO.setAlpacaSecretKey(userTradingAccountAlpacaRequest.getSecret());
            userConfigurationDTO.setIsActiveTradingAccount(true);
            userConfigurationService.updateUserConfiguration(userConfigurationDTO);
        }, () -> {
            System.out.println("User is not present");
            throw new RuntimeException("User not found");
        });
        return "user updated";
    }

    @Override
    public String deleteUser(Long Id) {
        return null;
    }

}
