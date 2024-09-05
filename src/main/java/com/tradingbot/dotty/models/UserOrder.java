package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
public class UserOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_order_id_seq")
    @SequenceGenerator(name = "user_order_id_seq", sequenceName = "user_order_id_seq", allocationSize = 1)
    private Long userOrderId;

    @Column(name="alpacaOrderId")
    private String alpacaOrderId;

    @Column(name="filled")
    private String filled;

    @Column(name="filledQty")
    private Double filledQty;

    @Column(name="filledAvgPrice")
    private Double filledAvgPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private Orders orders;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private int version;

}
