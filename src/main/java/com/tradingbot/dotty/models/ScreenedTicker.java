package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
public class ScreenedTicker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long screenedTickerId;

    @Column(name="name")
    private String name;

    @Column(name="symbol")
    private String symbol;

    @Column(name="sector")
    private String sector;

    @Column(name="exchangeShortName")
    private String exchangeShortName;

    @Column(name="beta")
    private float beta;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private int version;

}