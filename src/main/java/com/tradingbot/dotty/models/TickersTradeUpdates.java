package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TickersTradeUpdates {

    @Id
    @GeneratedValue(generator = "tickersTradeUpdatesSeq")
    @SequenceGenerator(name = "tickersTradeUpdates", sequenceName = "tickersTradeUpdatesSeq")
    private long tickerTradesId;

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

}
