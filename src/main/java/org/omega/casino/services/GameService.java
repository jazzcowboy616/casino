package org.omega.casino.services;

import org.omega.casino.controllers.CustomerGameMapper;
import org.omega.casino.dtos.GameDTO;
import org.omega.casino.dtos.GameFilter;
import org.omega.casino.entities.Game;
import org.omega.casino.entities.Round;
import org.omega.casino.repositories.GameRepository;
import org.omega.casino.repositories.RoundRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService {
    private final CustomerGameMapper mapper;

    private final GameRepository repo;
    private final RoundRepository roundRepo;

    public GameService(CustomerGameMapper mapper, GameRepository repo, RoundRepository roundRepo) {
        this.mapper = mapper;
        this.repo = repo;
        this.roundRepo = roundRepo;
    }

    @Transactional(readOnly = true)
    public Page<GameDTO> getAll(GameFilter filter, Pageable pageable) {
        return repo.findAll(filter.toPredicate(), pageable)
                .map(GameDTO::new);
    }

    @Transactional(readOnly = true)
    public Game getGame(Long id) {
        Game game = repo.getReferenceById(id);
        return game;
    }

    @Transactional
    public Game createGame(Game game) {
        return repo.save(game);
    }

    @Transactional
    public Game updateGame(Game updateGame) {
        Game game = repo.getReferenceById(updateGame.getId());
        mapper.updateCustomerFromReq(updateGame, game);
        return repo.save(game);
    }

    @Transactional
    public void deleteGame(Long id) {
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Round getRound(Long id) {
        return roundRepo.getReferenceById(id);
    }

    @Transactional
    public Round createRound(Long id) {
        Game game = repo.getReferenceById(id);
        Round round = new Round();
        round.setGame(game);
        return roundRepo.save(round);
    }
}
