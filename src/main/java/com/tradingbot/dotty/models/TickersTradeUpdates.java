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
public class TickersTradeUpdates {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticker_trades_id_seq")
    @SequenceGenerator(name = "ticker_trades_id_seq", sequenceName = "ticker_trades_id_seq", allocationSize = 1)
    private Long tickerTradesId;

    @Column(name="name")
    private String name;

    @Column(name="symbol")
    private String symbol;

    @Column(name="isActive")
    private Boolean isActive;

    @OneToOne
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "screenedTickerId")
    private ScreenedTicker screenedTicker;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private int version;

}
