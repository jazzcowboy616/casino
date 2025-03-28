package org.omega.casino.controllers;

import org.omega.casino.dtos.PlayerBetSummaryDTO;
import org.omega.casino.dtos.PlayerDTO;
import org.omega.casino.entities.Player;
import org.omega.casino.repositories.PlayerRepository;
import org.omega.casino.services.BetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    private final BetService betService;
    private final PlayerRepository repo;
    private final CustomerPlayerMapper mapper;

    public PlayerController(CustomerPlayerMapper mapper, PlayerRepository repo, BetService betService) {
        this.mapper = mapper;
        this.repo = repo;
        this.betService = betService;
    }

    /**
     * Get an user by id
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<PlayerDTO> getById(@PathVariable Long id) {
        PlayerDTO playerDTO = new PlayerDTO();
        mapper.updateCustomerFromEntity(repo.getReferenceById(id), playerDTO);
        return ResponseEntity.ok(playerDTO);
    }

    @PutMapping("{id}/deposit")
    public ResponseEntity<PlayerDTO> deposit(@PathVariable Long id) {
        Player player = repo.getReferenceById(id);
        BigDecimal randomBalance = BigDecimal.valueOf(Math.random()) // generate random between 0 and 1
                .multiply(BigDecimal.valueOf(100)) // multiply by 100 to get 0-100 random number
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal balance = player.getBalance();
        player.setBalance(balance.add(randomBalance));
        PlayerDTO playerDTO = new PlayerDTO();
        mapper.updateCustomerFromEntity(repo.save(player), playerDTO);
        return ResponseEntity.ok(playerDTO);
    }

    @GetMapping("{id}/bet_summary")
    public ResponseEntity<PlayerBetSummaryDTO> getPlayerBetSummary(@PathVariable Long id) {
        return ResponseEntity.ok(betService.getPlayerBetSummary(id));
    }
}
