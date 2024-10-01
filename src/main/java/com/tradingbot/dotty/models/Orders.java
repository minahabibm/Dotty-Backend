package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_ticker_id_seq")
    @SequenceGenerator(name = "order_ticker_id_seq", sequenceName = "order_ticker_id_seq", allocationSize = 1)
    private Long orderTickerId;

    @Column(name="symbol")
    private String symbol;

    @Column(name="tradeType")
    private String tradeType;

    @Column(name="quantity")
    private Long quantity;

    @Column(name="entryPrice")
    private Float entryPrice;

    @Column(name="entryTime")
    private LocalDateTime entryTime;

    @Column(name="active")
    private Boolean active;

    @ManyToOne//(fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "positionTrackerId")
    private PositionTracker positionTracker;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private int version;

}
