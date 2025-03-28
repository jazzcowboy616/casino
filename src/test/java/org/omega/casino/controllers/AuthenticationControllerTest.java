package org.omega.casino.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.omega.casino.dtos.LoginPlayerDTO;
import org.omega.casino.dtos.RegisterPlayerDTO;
import org.omega.casino.entities.Player;
import org.omega.casino.services.AuthenticationService;
import org.omega.casino.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private AuthenticationService authenticationService;

    @Value("${security.api.secret-key}")
    private String API_KEY;

    @Test
    public void testRegister() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        RegisterPlayerDTO dto = new RegisterPlayerDTO();
        dto.setPassword("123456");
        dto.setRepeatPassword("123456");
        dto.setName("test");
        dto.setUsername("test");
        dto.setBirthDate("1990-01-01");

        RegisterPlayerDTO invalidDto = dto.clone();
        invalidDto.setRepeatPassword("111111");

        Player player = new Player();
        player.setId(1L);
        player.setName(dto.getName());
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; // or DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(dto.getBirthDate(), formatter);
        player.setBirthDate(birthDate);
        player.setPassword("$2a$10$7LBjtBGe9/Val.xOi1EH9ejHIxWcMv0wX3MjpJew8i0h1fTZR1WRy");
        when(authenticationService.register(dto)).thenReturn(player);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-api-key",API_KEY)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(player.getId()))
                .andExpect(jsonPath("$.name").value(player.getName()))
                .andExpect(jsonPath("$.username").value(player.getUsername()))
                .andExpect(jsonPath("$.birthDate").value(player.getBirthDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        verify(authenticationService, times(1)).register(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-api-key",API_KEY)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
        verify(authenticationService, times(0)).register(invalidDto);
    }

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-api-key",API_KEY)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        String token = "generated-token";
        Player player = new Player();
        when(authenticationService.authenticate(any(LoginPlayerDTO.class))).thenReturn(player);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token);
        when(jwtService.getExpirationTime()).thenReturn(86400000L);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-api-key",API_KEY)
                        .content("{\"username\":\"test\",\"password\": \"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.expiresIn").value(86400000L));

        verify(authenticationService, times(1)).authenticate(any(LoginPlayerDTO.class));
        verify(jwtService, times(1)).generateToken(any(UserDetails.class));
        verify(jwtService, times(1)).getExpirationTime();
    }
}
