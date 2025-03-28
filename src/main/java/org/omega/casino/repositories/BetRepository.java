package org.omega.casino.repositories;

import org.omega.casino.entities.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long>, JpaSpecificationExecutor<Bet> {
    @Query("SELECT b FROM Bet b WHERE b.round.id = :roundId ORDER BY b.player.id")
    List<Bet> findBetsByRound(@Param("roundId") Long roundId);

    @Query("""
    SELECT COUNT(b), COALESCE(SUM(b.amount), 0), COALESCE(SUM(CASE WHEN b.win = TRUE THEN b.winAmount ELSE 0 END), 0)
    FROM Bet b WHERE b.player.id = :playerId
""")
    List getPlayerBetSummary(@Param("playerId") Long playerId);
}
