package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmailAddress(String email);
    Optional<Users> findByLoginUid(String loginUid);
    Optional<Users> findByUserConfiguration_UserConfigurationId(Long userConfigurationId);

}