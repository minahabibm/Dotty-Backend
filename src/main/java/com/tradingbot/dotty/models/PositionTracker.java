package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity
@Data
public class PositionTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long positionTrackerId;

    @Column(name="symbol")
    private String symbol;

    @Column(name="typeOfTrade")
    private String typeOfTrade;

    @Column(name="active")
    private Boolean active;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
