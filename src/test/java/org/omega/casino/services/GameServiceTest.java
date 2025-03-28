package org.omega.casino.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.omega.casino.dtos.GameDTO;
import org.omega.casino.dtos.GameFilter;
import org.omega.casino.entities.Game;
import org.omega.casino.entities.Round;
import org.omega.casino.repositories.GameRepository;
import org.omega.casino.repositories.RoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class GameServiceTest {
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private RoundRepository roundRepo;

    @Autowired
    @InjectMocks
    private GameService service;

    @BeforeEach
    public void setup() {
    }

    @AfterEach
    public void clearData() {
        roundRepo.deleteAll();
        gameRepo.deleteAll();
    }

    @Test
    @Transactional
    @Rollback
    public void testCreateGame() throws Exception {
        Game game = TestUtils.newGame("game1", 0.45, 2.0, 20, 200);
        service.createGame(game);
        Game res = gameRepo.getReferenceById(game.getId());
        Assertions.assertEquals(game.getId(), res.getId());
        Assertions.assertEquals(game.getWinRate(), res.getWinRate());
        Assertions.assertEquals(game.getWinMultiplier(), res.getWinMultiplier());
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateGame() throws Exception {
        Game game = TestUtils.newGame("game1", 0.45, 2.0, 20, 200);
        gameRepo.save(game);
        game.setName("updatedGame");
        Game res = service.updateGame(game);
        Assertions.assertEquals(game.getId(), res.getId());
        Assertions.assertEquals("updatedGame", res.getName());
        Assertions.assertEquals(game.getWinRate(), res.getWinRate());
        Assertions.assertEquals(game.getWinMultiplier(), res.getWinMultiplier());
    }

    @Test
    @Transactional
    @Rollback
    public void testCreateRound() throws Exception {
        gameRepo.save(TestUtils.newGame("game1", 0.45, 2.0, 20, 200));
        List<Game> games = gameRepo.findAll();
        Game game = games.get(0);
        Round round = service.createRound(game.getId());
        Assertions.assertEquals(game.getId(), round.getGame().getId());
        Assertions.assertEquals(game.getName(), round.getGame().getName());
        Assertions.assertFalse(round.getSettled());
    }

    @Test
    @Transactional
    @Rollback
    public void testSearchGames() throws Exception {
        gameRepo.save(TestUtils.newGame("game1", 0.45, 2.5, 20, 200));
        gameRepo.save(TestUtils.newGame("game2", 0.3, 3.0, 30, 100));
        gameRepo.save(TestUtils.newGame("game3", 0.62, 2.0, 40, 150));

        GameFilter filter = new GameFilter("name=2");
        Pageable pageable = PageRequest.of(0, 10);
        Page<GameDTO> res = service.getAll(filter, pageable);

        Assertions.assertEquals(1, res.getContent().size());
        Assertions.assertEquals("game2", res.getContent().get(0).getName());

        filter = new GameFilter("name=fake");
        res = service.getAll(filter, pageable);
        Assertions.assertEquals(0, res.getContent().size());

        filter = new GameFilter("winRate between 0.3 0.5");
        res = service.getAll(filter, pageable);
        Assertions.assertEquals(2, res.getContent().size());

        filter = new GameFilter("minBet>=30");
        res = service.getAll(filter, pageable);
        Assertions.assertEquals(2, res.getContent().size());

        filter = new GameFilter("minBet>30");
        res = service.getAll(filter, pageable);
        Assertions.assertEquals(1, res.getContent().size());

        filter = new GameFilter("maxBet<150");
        res = service.getAll(filter, pageable);
        Assertions.assertEquals(1, res.getContent().size());

        filter = new GameFilter("maxBet<=150");
        res = service.getAll(filter, pageable);
        Assertions.assertEquals(2, res.getContent().size());

        filter = new GameFilter("winRate between 0.3 0.7,winMultiplier>=2.5,maxBet<180");
        res = service.getAll(filter, pageable);
        Assertions.assertEquals(1, res.getContent().size());

        filter = new GameFilter("winRate between 0.3 0.7,winMultiplier>=2.5,maxBet<=200");
        res = service.getAll(filter, pageable);
        Assertions.assertEquals(2, res.getContent().size());
    }

}
