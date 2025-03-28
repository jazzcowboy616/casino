package org.omega.casino.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.omega.casino.entities.Game;
import org.springframework.data.jpa.domain.Specification;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameFilter {
    private String query;

    public Specification<Game> toPredicate() {
        return GameSpecifications.parse(query);
    }
}
