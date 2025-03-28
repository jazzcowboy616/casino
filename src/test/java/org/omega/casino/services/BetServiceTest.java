package org.omega.casino.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
@ActiveProfiles("test")
public class BetServiceTest {
    @Autowired
    private GameService gameService;
    @Autowired
    private BetService service;
    @Autowired
    private BetRepository repo;
    @Autowired
    private PlayerRepository playerRepo;
    @Autowired
    private RoundRepository roundRepo;
    @Autowired
    private GameRepository gameRepo;

    private Player player;
    private Game game;

    @BeforeEach
    public void initData() {
        this.player = playerRepo.save(TestUtils.newPlayer("Jone", "jone", "pwd", "1980-01-01", 100));
        this.game = gameRepo.save(TestUtils.newGame("game1", 0.45, 2.0, 20, 200));
    }

    @AfterEach
    public void clearData() {
        repo.deleteAll();
        roundRepo.deleteAll();
        gameRepo.deleteAll();
        playerRepo.deleteAll();
    }

    @Test
    public void testPlaceBet() throws Exception {
        Round round = roundRepo.save(TestUtils.newRound(game, true));
        final BetDTO emptyBetDTO = new BetDTO();
        emptyBetDTO.setRoundId(round.getId());
        Assertions.assertThrows(IllegalStateException.class, ()->service.placeBet(emptyBetDTO));

        round = gameService.createRound(game.getId());
        final BetDTO invalidBetDTO = TestUtils.newBetDTO(player.getId(), round.getId(), 10);
        Assertions.assertThrows(IllegalArgumentException.class, ()->service.placeBet(invalidBetDTO));

        final BetDTO infficientBetDTO = TestUtils.newBetDTO(player.getId(), round.getId(), 150);
        Assertions.assertThrows(IllegalStateException.class, ()->service.placeBet(infficientBetDTO));

        BetDTO betDTO = TestUtils.newBetDTO(player.getId(), round.getId(), 40);
        Bet bet = service.placeBet(betDTO);
        Bet res = repo.findById(bet.getId()).get();
        player = playerRepo.findById(player.getId()).get();
        Assertions.assertEquals(BigDecimal.valueOf(40).setScale(2, RoundingMode.HALF_DOWN), res.getAmount());
        Assertions.assertEquals(player.getId(), res.getPlayer().getId());
        Assertions.assertEquals(round.getId(), res.getRound().getId());
        Assertions.assertEquals(game.getId(), res.getRound().getGame().getId());
        Assertions.assertEquals(BigDecimal.valueOf(60).setScale(2, RoundingMode.HALF_DOWN), player.getBalance());
    }

    @Test
    public void testSolveRound() throws Exception {
        Round round = gameService.createRound(game.getId());
        BetDTO betDTO = TestUtils.newBetDTO(player.getId(), round.getId(), 20);
        service.placeBet(betDTO);
        betDTO = TestUtils.newBetDTO(player.getId(), round.getId(), 30);
        service.placeBet(betDTO);
        betDTO = TestUtils.newBetDTO(player.getId(), round.getId(), 25);
        service.placeBet(betDTO);
        service.solveRound(round.getId());

        List<Bet> bets = repo.findAll();
        Assertions.assertEquals(3, bets.size());
        BigDecimal winnings = BigDecimal.ZERO;
        BigDecimal betAmount = BigDecimal.ZERO;
        for (Bet bet : bets) {
            betAmount = betAmount.add(bet.getAmount());
            winnings = winnings.add(bet.getWinAmount());
            if(bet.getWin()) {
                Assertions.assertEquals(0, bet.getAmount().multiply(BigDecimal.valueOf(game.getWinMultiplier()))
                        .compareTo(bet.getWinAmount()));
            } else {
                Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(bet.getWinAmount()));
            }
        }
        BigDecimal totalAmount = player.getBalance().add(winnings).subtract(betAmount);
        player = playerRepo.findById(player.getId()).get();
        Assertions.assertEquals(0, totalAmount.compareTo(player.getBalance()));
    }

    @Test
    public void testGetPlayerBetSummary() throws Exception {
        Round round = gameService.createRound(game.getId());
        BetDTO betDTO = TestUtils.newBetDTO(player.getId(), round.getId(), 20);
        service.placeBet(betDTO);
        betDTO = TestUtils.newBetDTO(player.getId(), round.getId(), 30);
        service.placeBet(betDTO);
        betDTO = TestUtils.newBetDTO(player.getId(), round.getId(), 25);
        service.placeBet(betDTO);
        service.solveRound(round.getId());


        PlayerBetSummaryDTO summary = service.getPlayerBetSummary(player.getId());
        List<Bet> bets = repo.findAll();
        Assertions.assertEquals(3, summary.getTotalBets());
        Assertions.assertEquals(0, BigDecimal.valueOf(75).compareTo(summary.getTotalBetAmount()));
        if(bets.get(0).getWin()) {
            Assertions.assertEquals(0, BigDecimal.valueOf(150).compareTo(summary.getTotalWinnings()));
        } else {
            Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(summary.getTotalWinnings()));
        }
    }

    @Test
    public void testConcurrentPlaceBets() throws InterruptedException, ExecutionException {
        Round round = gameService.createRound(game.getId());
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Exception>> results = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            results.add(executor.submit(() -> {
                try {
                    service.placeBet(TestUtils.newBetDTO(player.getId(), round.getId(), 10));
                    return null;
                } catch (Exception e) {
                    return e;
                }
            }));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        long failures = results.stream().filter(f -> {
            try {
                return f.get() != null;
            } catch (Exception e) {
                return false;
            }
        }).count();

        Assertions.assertTrue(failures > 0); // Some bets should fail due to insufficient balance
    }

}
