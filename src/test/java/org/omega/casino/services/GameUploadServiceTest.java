package org.omega.casino.services;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.omega.casino.entities.Game;
import org.omega.casino.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@SpringBootTest
public class GameUploadServiceTest {
    @MockBean
    private GameRepository gameRepository;
    @MockBean
    private MultipartFile file;

    @Autowired
    @InjectMocks
    private GameUploadService service;

    @Captor
    private ArgumentCaptor<List<Game>> gameListCaptor;

    private static final String VALID_XML = """
        <games>
            <game>
                <name>Blackjack</name>
                <winRate>0.45</winRate>
                <winMultiplier>2.0</winMultiplier>
                <minBet>5.00</minBet>
                <maxBet>100.00</maxBet>
            </game>
        </games>
    """;

    private static final String MALFORMED_XML = """
        <games>
            <game>
                <name>Roulette
        </games>
    """;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testParseAndSaveGamesSuccessful() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(VALID_XML.getBytes());
        when(file.getInputStream()).thenReturn(inputStream);
        when(gameRepository.findAllGameNames()).thenReturn(Set.of("Roulette"));  // Blackjack is new

        List<Game> savedGames = service.parseAndSaveGames(file);

        verify(gameRepository, times(1)).saveAll(gameListCaptor.capture());
        List<Game> games = gameListCaptor.getValue();

        Assertions.assertEquals(1, games.size());
        Assertions.assertEquals("Blackjack", games.get(0).getName());
    }

    @Test
    void testParseAndSaveGamesSkipsDuplicateGames() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(VALID_XML.getBytes());
        when(file.getInputStream()).thenReturn(inputStream);
        when(gameRepository.findAllGameNames()).thenReturn(Set.of("Blackjack"));  // Already exists

        List<Game> savedGames = service.parseAndSaveGames(file);

        verify(gameRepository, times(1)).saveAll(gameListCaptor.capture());
        List<Game> games = gameListCaptor.getValue();

        Assertions.assertEquals(0, savedGames.size());  // No new games added
    }

    @Test
    void testParseAndSaveGamesWithMalformedXml() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(MALFORMED_XML.getBytes());
        when(file.getInputStream()).thenReturn(inputStream);

        Assertions.assertThrows(JAXBException.class, () -> service.parseAndSaveGames(file));
    }

    @Test
    void testParseAndSaveGamesWithEmptyXmlFile() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("<games></games>".getBytes());
        when(file.getInputStream()).thenReturn(inputStream);
        when(gameRepository.findAllGameNames()).thenReturn(Set.of());

        List<Game> savedGames = service.parseAndSaveGames(file);
        verify(gameRepository, times(0)).saveAll(anyList());

        Assertions.assertTrue(savedGames.isEmpty());
        verify(gameRepository, never()).saveAll(any());
    }


}
