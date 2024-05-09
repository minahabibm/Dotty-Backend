package com.tradingbot.dotty.models.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String userEmailAddress;
    private String pictureUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int version;

}
