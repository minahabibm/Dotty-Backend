package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "configuration_id_seq")
    @SequenceGenerator(name = "configuration_id_seq", sequenceName = "configuration_id_seq", allocationSize = 1)
    private Long configurationId;
//
//    @Column(name="key")
//    private String key;
//
//    @Column(name="value")
//    private String value;
//
//    @CreationTimestamp
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    private LocalDateTime updatedAt;
//
    @Version
    private int version;

}
