package org.omega.casino.controllers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.omega.casino.entities.Game;

@Mapper(componentModel = "Spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerGameMapper {
    /**
     * Copy DTO instance to Entity instance
     * field "author" is ignored
     *
     * @param game
     * @param entity
     */
    void updateCustomerFromReq(Game game, @MappingTarget Game entity);
}
