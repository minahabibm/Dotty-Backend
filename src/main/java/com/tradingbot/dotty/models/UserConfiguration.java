package com.tradingbot.dotty.models;

import com.tradingbot.dotty.utils.EncryptionUtil;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
public class UserConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_configuration_id_seq")
    @SequenceGenerator(name = "user_configuration_id_seq", sequenceName = "user_configuration_id_seq", allocationSize = 1)
    private Long userConfigurationId;

    @Column(name="activeTrading")
    private Boolean activeTrading;

    @Column(name="isActiveTradingAccount")
    private Boolean isActiveTradingAccount;

    @Column(name="alpacaApiKey")
    @Convert(converter = EncryptionUtil.class)
    private String alpacaApiKey;

    @Column(name="alpacaSecretKey")
    @Convert(converter = EncryptionUtil.class)
    private String alpacaSecretKey;

    @Column(name="alpacaPaperAccount")
    private Boolean alpacaPaperAccount;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private int version;

    @OneToOne(mappedBy = "userConfiguration")
    private Users users;

}
