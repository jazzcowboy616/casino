package org.omega.casino.services;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.omega.casino.controllers.GamesXmlWrapper;
import org.omega.casino.dtos.GameXmlDTO;
import org.omega.casino.entities.Game;
import org.omega.casino.repositories.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GameUploadService {
    private final GameRepository gameRepository;

    public GameUploadService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> parseAndSaveGames(MultipartFile file) throws JAXBException {
        try (InputStream inputStream = file.getInputStream()) {
            JAXBContext context = JAXBContext.newInstance(GamesXmlWrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            GamesXmlWrapper gamesWrapper = (GamesXmlWrapper) unmarshaller.unmarshal(inputStream);

            // Step 1: Fetch all existing game names in one DB query
            Set<String> existingGameNames = gameRepository.findAllGameNames();

            if(gamesWrapper.getGames() == null || gamesWrapper.getGames().isEmpty()) {
                return List.of();
            }

            // Step 2: Filter out games that already exist
            List<Game> newGames = gamesWrapper.getGames().stream()
                    .filter(gameDto -> !existingGameNames.contains(gameDto.getName())) // Skip duplicates
                    .map(this::convertToGameEntity)
                    .collect(Collectors.toList());

            // Step 3: Bulk save only new games
            return gameRepository.saveAll(newGames);
        } catch (Exception e) {
            throw new JAXBException("Failed to process XML file", e);
        }
    }

    private Game convertToGameEntity(GameXmlDTO dto) {
        Game game = new Game();
        game.setName(dto.getName());
        game.setDescription(dto.getDescription());
        game.setWinRate(dto.getWinRate());
        game.setWinMultiplier(dto.getWinMultiplier());
        game.setMinBet(dto.getMinBet());
        game.setMaxBet(dto.getMaxBet());
        return game;
    }
}
