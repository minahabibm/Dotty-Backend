package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Configuration {

    public Configuration(String name, String configKey, String configValue) {
        this.name = name;
        this.configKey = configKey;
        this.configValue = configValue;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "configuration_id_seq")
    @SequenceGenerator(name = "configuration_id_seq", sequenceName = "configuration_id_seq", allocationSize = 1)
    private Long configurationId;

    @Column(name="name")
    private String name;

    @Column(name="configKey")
    private String configKey;

    @Column(name="configValue")
    private String configValue;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private int version;

}
