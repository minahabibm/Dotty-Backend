package com.tradingbot.dotty.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String nickname;
    private String loginUid;
    private String pictureUrl;
    private UserConfigurationDTO userConfigurationDTO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int version;

}
