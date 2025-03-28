package org.omega.casino.controllers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.omega.casino.dtos.PlayerDTO;
import org.omega.casino.entities.Game;
import org.omega.casino.entities.Player;

@Mapper(componentModel = "Spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerPlayerMapper {
    /**
     * Copy Entity instance to DTO instance
     *
     * @param game
     * @param entity
     */
    void updateCustomerFromEntity(Player game, @MappingTarget PlayerDTO entity);
}
