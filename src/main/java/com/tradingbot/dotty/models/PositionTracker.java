package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Data
@Entity
public class PositionTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "position_tracker_id_seq")
    @SequenceGenerator(name = "position_tracker_id_seq", sequenceName = "position_tracker_id_seq", allocationSize = 1)
    private Long positionTrackerId;

    @Column(name="symbol")
    private String symbol;

    @Column(name="typeOfTrade")
    private String typeOfTrade;

    @Column(name="Indicator")
    private String indicator;

    @Column(name="active")
    private Boolean active;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private int version;

}
