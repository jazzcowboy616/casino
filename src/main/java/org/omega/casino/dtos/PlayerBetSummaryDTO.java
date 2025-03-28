package org.omega.casino.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PlayerBetSummaryDTO {
    private Long playerId;
    private String name;
    private long totalBets;
    private BigDecimal totalBetAmount;
    private BigDecimal totalWinnings;
}
