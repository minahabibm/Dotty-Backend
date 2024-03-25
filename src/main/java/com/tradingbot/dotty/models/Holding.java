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
public class Holding {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long holdingTickerId;

    @Column(name="symbol")
    private String symbol;

    @Column(name="typeOfTrade")
    private String typeOfTrade;

    @Column(name="entryPrice")
    private Float entryPrice;

    @Column(name="ExitPrice")
    private Float ExitPrice;

    @Column(name="quantity")
    private Long quantity;

    @Column(name="startAt")
    private LocalDateTime startAt;

    @Column(name="endAt")
    private LocalDateTime endAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
