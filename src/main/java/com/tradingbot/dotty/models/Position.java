package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;

@Data
@Entity
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "position_id_seq")
    @SequenceGenerator(name = "position_id_seq", sequenceName = "position_id_seq", allocationSize = 1)
    private Long positionId;

    @Column(name="symbol")
    private String symbol;

    @Column(name="tai")
    private String tai;

    @Column(name="taiValue")
    private Float taiValue;

    @Column(name="intervals")
    private LocalDateTime intervals;

    @Column(name="open")
    private Float open;

    @Column(name="high")
    private Float high;

    @Column(name="low")
    private Float low;

    @Column(name="close")
    private Float close;

    @Column(name="volume")
    private Long volume;

    @ManyToOne//(fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "positionTracker")
    private PositionTracker positionTracker;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @Version
    private int version;

}
