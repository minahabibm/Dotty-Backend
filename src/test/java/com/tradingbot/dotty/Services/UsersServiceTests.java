package com.tradingbot.dotty.Services;

import com.tradingbot.dotty.models.UserConfiguration;
import com.tradingbot.dotty.models.Users;
import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.UsersDTO;
import com.tradingbot.dotty.repositories.UsersRepository;
import com.tradingbot.dotty.serviceImpls.UsersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTests {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UsersServiceImpl usersService;

    private Users user;
    private UsersDTO usersDTO;
    private UserConfiguration userConfiguration;
    private UserConfigurationDTO userConfigurationDTO;

    @BeforeEach
    void setUp() {
        // Initialize UserConfiguration
        userConfiguration = new UserConfiguration();
        userConfigurationDTO = new UserConfigurationDTO();

        // Initialize Users
        user = new Users();
        user.setUserId(1L);
        user.setFirstName("Joe");
        user.setLastName("Doe");
        user.setEmailAddress("test@example.com");
        user.setNickname("joed");
        user.setLoginUid("login123");
        user.setPictureUrl("http://example.com/picture.jpg");
        user.setUserConfiguration(userConfiguration);
        user.setCreatedAt(LocalDateTime.now().minusDays(1));
        user.setUpdatedAt(LocalDateTime.now());
        user.setVersion(1);

        // Initialize UsersDTO
        usersDTO = new UsersDTO();
        usersDTO.setUserId(1L);
        usersDTO.setFirstName("John");
        usersDTO.setLastName("Doe");
        usersDTO.setEmailAddress("test@example.com");
        usersDTO.setNickname("Joed");
        usersDTO.setLoginUid("login123");
        usersDTO.setPictureUrl("http://example.com/picture.jpg");
        usersDTO.setUserConfigurationDTO(userConfigurationDTO);
        usersDTO.setCreatedAt(LocalDateTime.now().minusDays(1));
        usersDTO.setUpdatedAt(LocalDateTime.now());
        usersDTO.setVersion(1);
    }

    @Test
    void testGetUsers() {
        when(usersRepository.findAll()).thenReturn(List.of(user));
        when(modelMapper.map(user, UsersDTO.class)).thenReturn(usersDTO);

        List<UsersDTO> result = usersService.getUsers();

        assertEquals(1, result.size());
        assertEquals(usersDTO, result.get(0));
        verify(usersRepository).findAll();
        verify(modelMapper).map(user, UsersDTO.class);
    }

    @Test
    void testGetUser() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UsersDTO.class)).thenReturn(usersDTO);

        Optional<UsersDTO> result = usersService.getUser(1L);

        assertEquals(Optional.of(usersDTO), result);
        verify(usersRepository).findById(1L);
        verify(modelMapper).map(user, UsersDTO.class);
    }

    @Test
    void testGetUserByEmail() {
        when(usersRepository.findByEmailAddress("test@example.com")).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UsersDTO.class)).thenReturn(usersDTO);

        Optional<UsersDTO> result = usersService.getUserByEmail("test@example.com");

        assertEquals(Optional.of(usersDTO), result);
        verify(usersRepository).findByEmailAddress("test@example.com");
        verify(modelMapper).map(user, UsersDTO.class);
    }

    @Test
    void testGetUserByUserConfigurationId() {
        when(usersRepository.findByUserConfiguration_UserConfigurationId(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UsersDTO.class)).thenReturn(usersDTO);

        Optional<UsersDTO> result = usersService.getUserByUserConfigurationId(1L);

        assertEquals(Optional.of(usersDTO), result);
        verify(usersRepository).findByUserConfiguration_UserConfigurationId(1L);
        verify(modelMapper).map(user, UsersDTO.class);
    }

    @Test
    void testInsertUser() {
        when(usersRepository.save(any(Users.class))).thenReturn(user);
        when(modelMapper.map(usersDTO, Users.class)).thenReturn(user);
        when(modelMapper.map(user, UsersDTO.class)).thenReturn(usersDTO);

        Optional<UsersDTO> result = usersService.insertUser(usersDTO);

        assertEquals(Optional.of(usersDTO), result);
        verify(usersRepository).save(any(Users.class));
        verify(modelMapper).map(usersDTO, Users.class);
        verify(modelMapper).map(user, UsersDTO.class);
    }

    @Test
    void testUpdateUser() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usersRepository.save(any(Users.class))).thenReturn(user);
        when(modelMapper.map(user, UsersDTO.class)).thenReturn(usersDTO);

        Optional<UsersDTO> result = usersService.updateUser(usersDTO);

        assertEquals(Optional.of(usersDTO), result);
        verify(usersRepository).findById(1L);
        verify(usersRepository).save(any(Users.class));
        verify(modelMapper).map(user, UsersDTO.class);
    }

    @Test
    void testDeleteUser() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        usersService.deleteUser(1L);

        verify(usersRepository).findById(1L);
        verify(usersRepository).delete(user);
    }

    @Test
    void testDeleteUserNotFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> usersService.deleteUser(1L));

        verify(usersRepository).findById(1L);
        verify(usersRepository, never()).delete(any(Users.class));
    }

}
