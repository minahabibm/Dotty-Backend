package com.tradingbot.dotty.models.dto;

import com.tradingbot.dotty.models.PositionTracker;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionDTO {

    private long positionId;
    private String symbol;
    private String tai;
    private String taiValue;
    private LocalDateTime intervals;
    private String open;
    private String high;
    private String low;
    private String close;
    private Long volume;
    private String typeOfTrade;
    private PositionTrackerDTO positionTrackerDTO;
    private LocalDateTime createdOn;

}
