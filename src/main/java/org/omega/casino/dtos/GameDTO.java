package org.omega.casino.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.omega.casino.entities.Game;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String description;

    public GameDTO(Game game) {
        this.id = game.getId();
        this.name = game.getName();
        this.description = game.getDescription();
    }
}
