package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(name="firstName")
    private String firstName;

    @Column(name="lastName")
    private String lastName;

    @Column(name="userEmailAddress")
    private String emailAddress;

    @Column(name="nickName")
    private String nickname;

    @Column(name="loginType")
    private String loginType;

    @Column(name="pictureUrl")
    private String pictureUrl;

    @OneToOne
    @JoinColumn(name = "userConfigurationId")
    private UserConfiguration userConfiguration;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private int version;
}
