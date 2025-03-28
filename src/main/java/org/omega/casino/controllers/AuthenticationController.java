package org.omega.casino.controllers;

import jakarta.validation.Valid;
import org.omega.casino.dtos.LoginPlayerDTO;
import org.omega.casino.dtos.RegisterPlayerDTO;
import org.omega.casino.entities.Player;
import org.omega.casino.responses.LoginResponse;
import org.omega.casino.services.AuthenticationService;
import org.omega.casino.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    /**
     * create a new user account.
     *
     * @param registerPlayerDto
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<Player> register(@Valid @RequestBody RegisterPlayerDTO registerPlayerDto) {
        if (!registerPlayerDto.getPassword().equals(registerPlayerDto.getRepeatPassword()))
            throw new IllegalArgumentException("Re-entered password not consistent to password");
        Player registeredUser = authenticationService.register(registerPlayerDto);

        return ResponseEntity.ok(registeredUser);
    }

    /**
     * log in to an existing user account and receive an access token.
     * @param loginPlayerDto
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginPlayerDTO loginPlayerDto) {
        Player authenticatedUser = authenticationService.authenticate(loginPlayerDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
