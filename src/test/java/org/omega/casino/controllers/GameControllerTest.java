package org.omega.casino.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omega.casino.dtos.GameDTO;
import org.omega.casino.dtos.GameFilter;
import org.omega.casino.entities.Game;
import org.omega.casino.entities.Player;
import org.omega.casino.services.GameService;
import org.omega.casino.services.GameUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {
    @Value("${security.api.secret-key}")
    private String API_KEY;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService service;

    @MockBean
    private PagedResourcesAssembler<GameDTO> assembler;

    @MockBean
    private GameUploadService gameUploadService;

    public void setUpMockSecurityContext() {
        UserDetails userDetails = new Player(1L, "jone", "111", "Jone", LocalDate.parse("1980-01-01", DateTimeFormatter.ISO_LOCAL_DATE));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @BeforeEach
    public void setup() {
        setUpMockSecurityContext();
    }

    @Test
    public void testSearchGame() throws Exception {
        GameDTO game = new GameDTO();
        game.setId(1L);
        game.setName("game1");
        Pageable pageable = PageRequest.of(0, 10);
        Page<GameDTO> notes = new PageImpl<>(Collections.singletonList(game), pageable, 1);
        PagedModel<GameDTO> pagedModel = PagedModel.of(Collections.singletonList(game), new PagedModel.PageMetadata(1, 0, 1));

        when(service.getAll(any(GameFilter.class), any(Pageable.class))).thenReturn(notes);
        when(assembler.toModel(any(Page.class))).thenReturn(pagedModel);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/games/search?query=name=21,winRate between 0.4 0.7,minBet>=2,maxBet<=10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-api-key",API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.gameDTOList[0]").exists());
        verify(service, times(1)).getAll(any(GameFilter.class), any(Pageable.class));
    }

    @Test
    public void testGetGameList() throws Exception {
        GameDTO game = new GameDTO();
        game.setId(1L);
        game.setName("game1");
        Pageable pageable = PageRequest.of(0, 10);
        Page<GameDTO> notes = new PageImpl<>(Collections.singletonList(game), pageable, 1);
        Page<GameDTO> emptyNotes = new PageImpl<>(Collections.emptyList(), pageable, 1);
        PagedModel<GameDTO> pagedModel = PagedModel.of(Collections.singletonList(game), new PagedModel.PageMetadata(1, 0, 1));
        PagedModel<GameDTO> emptyModel = PagedModel.of(Collections.emptyList(), new PagedModel.PageMetadata(0, 0, 0));
        when(service.getAll(any(GameFilter.class), any(Pageable.class))).thenReturn(notes);
        when(assembler.toModel(any(Page.class))).thenReturn(pagedModel);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-api-key",API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.gameDTOList[0]").exists());
        verify(service, times(1)).getAll(any(GameFilter.class), any(Pageable.class));

        when(service.getAll(any(GameFilter.class), any(Pageable.class))).thenReturn(emptyNotes);
        when(assembler.toModel(any(Page.class))).thenReturn(emptyModel);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-api-key",API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.gameDTOList[0]").doesNotExist());
    }

    @Test
    public void testGetGameById() throws Exception {
        Game mockGame = new Game(1L, "game1", "game1 description", 0.45, 2.0, BigDecimal.valueOf(20), BigDecimal.valueOf(200));
        when(service.getGame(1L)).thenReturn(mockGame);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/games/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-api-key",API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("game1"))
                .andExpect(jsonPath("$.winRate").value(0.45))
                .andExpect(jsonPath("$.winMultiplier").value(2.0))
                .andExpect(jsonPath("$.minBet").value(BigDecimal.valueOf(20)))
                .andExpect(jsonPath("$.maxBet").value(BigDecimal.valueOf(200)));

        verify(service).getGame(mockGame.getId());

        when(service.getGame(2L)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/games/2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-api-key",API_KEY))
                .andExpect(status().isNotFound());
    }
}
