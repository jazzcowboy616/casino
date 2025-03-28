package org.omega.casino.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omega.casino.dtos.PlayerBetSummaryDTO;
import org.omega.casino.dtos.PlayerDTO;
import org.omega.casino.entities.Player;
import org.omega.casino.repositories.PlayerRepository;
import org.omega.casino.services.BetService;
import org.omega.casino.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PlayerRepository repo;
    @MockBean
    private BetService betService;

    @Value("${security.api.secret-key}")
    private String API_KEY;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

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
    public void testGetPlayerById() throws Exception {
        Player mockPlayer = new Player(1L, "adam", "password", "Adam", LocalDate.parse("1992-07-01", DateTimeFormatter.ISO_LOCAL_DATE));
        when(repo.getReferenceById(1L)).thenReturn(mockPlayer);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/players/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-api-key",API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("adam"))
                .andExpect(jsonPath("$.name").value("Adam"))
                .andExpect(jsonPath("$.birthDate").value("1992-07-01"));

        verify(repo).getReferenceById(mockPlayer.getId());

        when(repo.getReferenceById(2L)).thenThrow(NoSuchElementException.class);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/players/2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-api-key",API_KEY))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDepoitForPlayer() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Player mockPlayer = new Player(1L, "adam", "password", "Adam", LocalDate.parse("1992-07-01", DateTimeFormatter.ISO_LOCAL_DATE));
        mockPlayer.setBalance(BigDecimal.valueOf(200L));
        when(repo.getReferenceById(1L)).thenReturn(mockPlayer);
        when(repo.save(mockPlayer)).thenReturn(mockPlayer);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/players/1/deposit")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-api-key",API_KEY)).andReturn();
        System.out.println(result.getResponse().getContentAsString());
        PlayerDTO res = mapper.readValue(result.getResponse().getContentAsString(), PlayerDTO.class);
        Assertions.assertNotEquals(BigDecimal.valueOf(200), res.getBalance());
    }

    @Test
    public void testGetPlayerSummary() throws Exception {
        PlayerBetSummaryDTO mockSummary = new PlayerBetSummaryDTO(1L, "Jone", 7, BigDecimal.valueOf(108), BigDecimal.valueOf(66));
        when(betService.getPlayerBetSummary(1L)).thenReturn(mockSummary);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/players/1/bet_summary")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-api-key",API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").value(1L))
                .andExpect(jsonPath("$.name").value("Jone"))
                .andExpect(jsonPath("$.totalBets").value(7))
                .andExpect(jsonPath("$.totalBetAmount").value(108))
                .andExpect(jsonPath("$.totalWinnings").value(66));
    }
}
