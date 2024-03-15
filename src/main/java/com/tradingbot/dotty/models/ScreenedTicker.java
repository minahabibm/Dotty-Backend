package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenedTicker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long screenedTickerId;

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

}