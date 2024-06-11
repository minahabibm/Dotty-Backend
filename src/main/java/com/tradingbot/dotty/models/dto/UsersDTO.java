package com.tradingbot.dotty.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String nickname;
    private String loginType;
    private String pictureUrl;
    private UserConfigurationDTO userConfigurationDTO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int version;

}
