package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;

@Data
@Entity
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long positionId;

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

    @Column(name="typeOfTrade")
    private String typeOfTrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "positionTrackerID")
    private PositionTracker positionTracker;

    @CreationTimestamp
    private LocalDateTime createdOn;

}
