package org.omega.casino.services;

import org.omega.casino.controllers.CustomerGameMapper;
import org.omega.casino.dtos.BetDTO;
import org.omega.casino.dtos.PlayerBetSummaryDTO;
import org.omega.casino.entities.Bet;
import org.omega.casino.entities.Game;
import org.omega.casino.entities.Player;
import org.omega.casino.entities.Round;
import org.omega.casino.repositories.BetRepository;
import org.omega.casino.repositories.GameRepository;
import org.omega.casino.repositories.PlayerRepository;
import org.omega.casino.repositories.RoundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BetService {
    private final BetRepository repo;
    private final PlayerRepository playerRepo;
    private final RoundRepository roundRepo;

    public BetService(BetRepository repo, PlayerRepository playerRepo, RoundRepository roundRepo) {
        this.repo = repo;
        this.playerRepo = playerRepo;
        this.roundRepo = roundRepo;
    }

    @Transactional
    public Bet placeBet(BetDTO betDto) {
        Round round = roundRepo.getReferenceById(betDto.getRoundId());
        if (round.getSettled()) {
            throw new IllegalStateException("Cannot bet on a finished round");
        }
        BigDecimal betAmount = betDto.getAmount();
        Game game = round.getGame();
        if (betAmount.compareTo(game.getMinBet()) < 0 || betAmount.compareTo(game.getMaxBet()) > 0) {
            throw new IllegalArgumentException("Bet amount out of range");
        }
        int rowsUpdated = playerRepo.decrementBalanceIfSufficient(betDto.getPlayerId(), betDto.getAmount());
        if (rowsUpdated == 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        Player player = playerRepo.getReferenceById(betDto.getPlayerId());
        Bet bet = new Bet();
        bet.setPlayer(player);
        bet.setRound(round);
        bet.setAmount(betDto.getAmount());
        bet.setBetAt(LocalDateTime.now());
        return repo.save(bet);
    }

    @Transactional
    public void solveRound(Long roundId) {
        Round round = roundRepo.getReferenceById(roundId);

        if (round.getSettled()) {
            throw new IllegalStateException("Round already settled");
        }

        List<Bet> unsettledBets = repo.findBetsByRound(roundId);

        Map<Long, BigDecimal> playerWinnings = new HashMap<>();

        boolean isWinner = false;
        for (Bet bet : unsettledBets) {
            Long playerId = bet.getPlayer().getId();
            // If the player won, all the bet placed by the player gain the amount*winMultiplier, otherwise, all bets reward 0
            if(!playerWinnings.containsKey(playerId))
                isWinner = Math.random() < bet.getRound().getGame().getWinRate();

            BigDecimal winnings = isWinner ? bet.getAmount().multiply(new BigDecimal(bet.getRound().getGame().getWinMultiplier())) : BigDecimal.ZERO;
            playerWinnings.put(playerId, playerWinnings.getOrDefault(bet.getPlayer().getId(), BigDecimal.ZERO).add(winnings));

            bet.setWin(isWinner);
            bet.setWinAmount(winnings);
        }

        repo.saveAll(unsettledBets);

        for (Map.Entry<Long, BigDecimal> entry : playerWinnings.entrySet()) {
            playerRepo.incrementBalance(entry.getKey(), entry.getValue());
        }

        // Mark the round as settled
        round.setSettled(true);
        roundRepo.save(round);
    }

    @Transactional(readOnly = true)
    public PlayerBetSummaryDTO getPlayerBetSummary(Long playerId) {
        Player player = playerRepo.getReferenceById(playerId);
        List result = repo.getPlayerBetSummary(playerId);
        if(result == null || result.isEmpty())
            return new PlayerBetSummaryDTO(player.getId(), player.getName(),0, BigDecimal.ZERO, BigDecimal.ZERO);

        Object[] summary = (Object[])result.get(0);
        long totalBets = (summary[0] != null) ? ((Long) summary[0]).longValue() : 0;
        BigDecimal totalBetAmount = (summary[1] != null) ? (BigDecimal) summary[1] : BigDecimal.ZERO;
        BigDecimal totalWinnings = (summary[2] != null) ? (BigDecimal) summary[2] : BigDecimal.ZERO;

        return new PlayerBetSummaryDTO(player.getId(), player.getName(), totalBets, totalBetAmount, totalWinnings);
    }
}
