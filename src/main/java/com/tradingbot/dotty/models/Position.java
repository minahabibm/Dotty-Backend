package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long positionId;

    @Column(name="symbol")
    private String symbol;

    @Column(name="tai")
    private String tai;

    @Column(name="taiValue")
    private String taiValue;

    @Column(name="intervals")
    private LocalDateTime intervals;

    @Column(name="open")
    private String open;

    @Column(name="high")
    private String high;

    @Column(name="low")
    private String low;

    @Column(name="close")
    private String close;

    @Column(name="volume")
    private Long volume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "positionTrackerId")
    private PositionTracker positionTracker;

    @CreationTimestamp
    private LocalDateTime createdOn;

}
