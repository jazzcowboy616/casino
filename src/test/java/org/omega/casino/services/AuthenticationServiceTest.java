package org.omega.casino.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.omega.casino.dtos.LoginPlayerDTO;
import org.omega.casino.dtos.RegisterPlayerDTO;
import org.omega.casino.entities.Player;
import org.omega.casino.repositories.PlayerRepository;
import org.omega.casino.exceptions.UserExistingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthenticationServiceTest {
    @MockBean
    private PlayerRepository playerRepository;
    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    @InjectMocks
    private AuthenticationService service;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignup() {
        RegisterPlayerDTO dto = new RegisterPlayerDTO();
        dto.setUsername("test");
        dto.setPassword("123456");
        dto.setBirthDate("1980-01-01");

        when(playerRepository.findByUsername(anyString())).thenReturn(Optional.of(new Player()));
        Assertions.assertThrows(UserExistingException.class, () -> service.register(dto));

        Player player = TestUtils.newPlayer("test", dto.getUsername(), dto.getPassword(), "1980-01-01", 100);

        when(playerRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        Player res = service.register(dto);

        Assertions.assertEquals(player.getName(), res.getName());
        Assertions.assertEquals(player.getUsername(), res.getUsername());
        Assertions.assertEquals(player.getPassword(), res.getPassword());
        Assertions.assertEquals(player.getBirthDate(), res.getBirthDate());
    }

    @Test
    public void testLogin() {
        LoginPlayerDTO dto = new LoginPlayerDTO();
        dto.setUsername("test");
        dto.setPassword("123456");
        when(playerRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(NoSuchElementException.class, () -> service.authenticate(dto));

        Player player = TestUtils.newPlayer("test", dto.getUsername(), dto.getPassword(), "1980-01-01", 100);
        when(playerRepository.findByUsername(anyString())).thenReturn(Optional.of(player));

        Player res = service.authenticate(dto);

        Assertions.assertEquals(player.getName(), res.getName());
        Assertions.assertEquals(player.getUsername(), res.getUsername());
        Assertions.assertEquals(player.getPassword(), res.getPassword());

        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(InternalAuthenticationServiceException.class);

        Assertions.assertThrows(AuthenticationException.class, () -> service.authenticate(dto));
    }
}
