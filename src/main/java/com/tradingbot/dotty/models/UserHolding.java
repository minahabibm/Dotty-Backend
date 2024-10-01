package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
public class UserHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_holding_id_seq")
    @SequenceGenerator(name = "user_holding_id_seq", sequenceName = "user_holding_id_seq", allocationSize = 1)
    private Long userHoldingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holdingId")
    private Holding holding;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private int version;

}
