package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Blob;
import java.time.LocalDateTime;

@Data
@Entity
public class Configuration {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
