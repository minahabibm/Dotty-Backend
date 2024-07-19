package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
public class UserConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userConfigurationId;

    @Column(name="activeTrading")
    private Boolean activeTrading;

    @Column(name="isActiveTradingAccount")
    private Boolean isActiveTradingAccount;

    @Column(name="alpacaApiKey")
    private String alpacaApiKey;

    @Column(name="alpacaSecretKey")
    private String alpacaSecretKey;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private int version;

    @OneToOne(mappedBy = "userConfiguration")
    private Users users;
}
