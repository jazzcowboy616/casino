package org.omega.casino.controllers;

import jakarta.validation.Valid;
import org.omega.casino.dtos.BetDTO;
import org.omega.casino.entities.Bet;
import org.omega.casino.services.BetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BetController {
    private final BetService service;

    public BetController(BetService service) {
        this.service = service;
    }

    /**
     * Place a bet for the player in a specific game round
     *
     * @param betDto
     * @return
     */
    @PostMapping("/bets")
    public ResponseEntity<BetDTO> placeBet(@Valid @RequestBody BetDTO betDto) {
        Bet bet = service.placeBet(betDto);
        return new ResponseEntity(new BetDTO(bet), HttpStatus.CREATED);
    }

    /**
     * Resolve a round of a game and settle all bets belongs to it
     *
     * @param id
     */
    @PutMapping("/rounds/{id}")
    public void resolveRound(@PathVariable Long id){
        service.solveRound(id);
    }
}
