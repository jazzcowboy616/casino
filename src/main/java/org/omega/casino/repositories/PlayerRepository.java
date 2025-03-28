package org.omega.casino.repositories;

import org.omega.casino.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>, CrudRepository<Player, Long> {
    Optional<Player> findByUsername(String username);

    @Modifying
    @Query("""
    UPDATE Player p 
    SET p.balance = p.balance - :betAmount 
    WHERE p.id = :playerId AND p.balance >= :betAmount
""")
    int decrementBalanceIfSufficient(@Param("playerId") Long playerId, @Param("betAmount") BigDecimal betAmount);

    @Modifying
    @Query("""
    UPDATE Player p
    SET p.balance = p.balance + :amount
    WHERE p.id = :playerId
""")
    void incrementBalance(Long playerId, BigDecimal amount);
}
