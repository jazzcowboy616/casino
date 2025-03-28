package org.omega.casino.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.omega.casino.entities.Bet;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BetDTO {
    private Long id;

    private Long playerId;

    private Long roundId;

    private BigDecimal amount;

    public BetDTO(Bet bet) {
        this.id = bet.getId();
        this.playerId = bet.getPlayer().getId();
        this.roundId = bet.getRound().getId();
        this.amount = bet.getAmount();
    }
}
