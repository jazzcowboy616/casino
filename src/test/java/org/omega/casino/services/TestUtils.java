package org.omega.casino.services;

import org.omega.casino.dtos.BetDTO;
import org.omega.casino.entities.Bet;
import org.omega.casino.entities.Game;
import org.omega.casino.entities.Player;
import org.omega.casino.entities.Round;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestUtils {

    public static Player newPlayer(String name, String username, String password, String birthDate, double balance) {
        Player player = new Player();
        player.setName(name);
        player.setUsername(username);
        player.setPassword(password);
        player.setBirthDate(LocalDate.parse(birthDate, DateTimeFormatter.ISO_LOCAL_DATE));
        player.setBalance(BigDecimal.valueOf(balance));
        player.setOnline(true);
        return player;
    }
    public static Game newGame(String name, double winRate, double winMul, double minBet, double maxBet) {
        Game game = new Game();
        game.setName(name);
        game.setWinRate(winRate);
        game.setWinMultiplier(winMul);
        game.setMinBet(BigDecimal.valueOf(minBet));
        game.setMaxBet(BigDecimal.valueOf(maxBet));
        return game;
    }

    public static Round newRound(Game game, Boolean settled) {
        Round round = new Round();
        round.setGame(game);
        round.setSettled(settled);
        return round;
    }

    public static Bet newBet(Player player, Round round, BigDecimal amount) {
        Bet bet = new Bet();
        bet.setPlayer(player);
        bet.setRound(round);
        bet.setAmount(amount);
        bet.setBetAt(LocalDateTime.now());
        return bet;
    }

    public static BetDTO newBetDTO(Long playerId, Long roundId, double amount) {
        BetDTO betDTO = new BetDTO();
        betDTO.setPlayerId(playerId);
        betDTO.setRoundId(roundId);
        betDTO.setAmount(BigDecimal.valueOf(amount));
        return betDTO;
    }
}
