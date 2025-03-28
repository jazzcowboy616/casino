package org.omega.casino.controllers;

import jakarta.validation.Valid;
import jakarta.xml.bind.JAXBException;
import org.omega.casino.dtos.GameDTO;
import org.omega.casino.dtos.GameFilter;
import org.omega.casino.entities.Game;
import org.omega.casino.entities.Round;
import org.omega.casino.services.GameService;
import org.omega.casino.services.GameUploadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService service;

    private final GameUploadService gameUploadService;

    public GameController(GameService service, GameUploadService gameUploadService) {
        this.service = service;
        this.gameUploadService = gameUploadService;
    }

    /**
     * search for game based on keywords.
     * GET /api/search?search={query}
     *
     * @param filter    search={query}, support fuzzy search, exact match, >=, <=, >, <, between
     * @param pageable
     * @param assembler
     * @return
     */
    @GetMapping("/search")
    public PagedModel<GameDTO> getAll(
            GameFilter filter,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable, PagedResourcesAssembler assembler
    ) {
        Page<GameDTO> games = service.getAll(filter, pageable);
        return assembler.toModel(games);
    }

    /**
     * Get a list of all games.
     * Only return the contents created by or shared to current user
     * @param pageable
     * @param assembler
     * @return
     */
    @GetMapping("")
    public PagedModel<GameDTO> getGameList(@PageableDefault(sort = "id", direction = Sort.Direction.ASC)
                                  Pageable pageable, PagedResourcesAssembler assembler) {
        return getAll(new GameFilter(), pageable, assembler);
    }

    /**
     * Get a game by ID for the authenticated user.
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGame(@PathVariable Long id) {
        Game game = service.getGame(id);
        return ResponseEntity.ok(game);
    }

    /**
     * Create a new game.
     * title and content are mandatory
     * @param gameDto
     * @return
     */
    @PostMapping("")
    public ResponseEntity<Game> createGame(@Valid @RequestBody Game gameDto) {
        Game game = service.createGame(gameDto);
        return new ResponseEntity(game, HttpStatus.CREATED);
    }

    /**
     * Update an existing game by ID for the authenticated user.
     * @param id
     * @param gameDto
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @RequestBody Game gameDto) {
        gameDto.setId(id);
        Game game = service.updateGame(gameDto);
        return ResponseEntity.ok(game);
    }

    /**
     * Delete a game by ID for the authenticated user.
     * @param id
     */
    @DeleteMapping("/{id}")
    public void deleteGame(@PathVariable Long id) {
        service.deleteGame(id);
    }

    /**
     * create a new round for a specific game
     * @param id
     */
    @GetMapping("/{id}/rounds/{roundId}")
    public ResponseEntity<Round> createRound(@PathVariable Long id, @PathVariable Long roundId) {
        Round round = service.getRound(roundId);
        return new ResponseEntity(round, HttpStatus.CREATED);
    }

    /**
     * create a new round for a specific game
     * @param id
     */
    @PostMapping("/{id}/rounds")
    public ResponseEntity<Round> createRound(@PathVariable Long id) {
        Round round = service.createRound(id);
        return new ResponseEntity(round, HttpStatus.CREATED);
    }

    @PostMapping("/upload")
    public ResponseEntity<List<Game>> uploadGames(@RequestParam("file") MultipartFile file) {
        try {
            List<Game> savedGames = gameUploadService.parseAndSaveGames(file);
            return ResponseEntity.ok(savedGames);
        } catch (JAXBException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
