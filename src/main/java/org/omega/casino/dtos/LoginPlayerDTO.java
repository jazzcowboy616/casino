package org.omega.casino.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginPlayerDTO {
    @NotNull
    private String username;
    @NotNull
    private String password;
}

