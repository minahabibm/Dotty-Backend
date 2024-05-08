package com.tradingbot.dotty.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;



    @Version
    private int version;
}
