package org.omega.casino.services;

import org.omega.casino.dtos.LoginPlayerDTO;
import org.omega.casino.dtos.RegisterPlayerDTO;
import org.omega.casino.entities.Player;
import org.omega.casino.exceptions.UserExistingException;
import org.omega.casino.repositories.PlayerRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            PlayerRepository playerRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Player register(RegisterPlayerDTO input) {
        Optional<Player> dbUser = playerRepository.findByUsername(input.getUsername());
        if (dbUser.isPresent())
            throw new UserExistingException(input.getUsername());
        var player = new Player();
        player.setName(input.getName());
        player.setUsername(input.getUsername());
        player.setPassword(passwordEncoder.encode(input.getPassword()));
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; // or DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(input.getBirthDate(), formatter);
        player.setBirthDate(birthDate);

        return playerRepository.save(player);
    }

    @Transactional
    public Player authenticate(LoginPlayerDTO input) {
        Player player = playerRepository.findByUsername(input.getUsername()).orElseThrow();
        player.setOnline(true);
        player.setLastLoginTime(LocalDateTime.now());
        playerRepository.save(player);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        player,
                        input.getPassword()
                )
        );

        return player;
    }
}
