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
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

}
