package org.omega.casino.controllers;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.omega.casino.dtos.GameXmlDTO;

import java.util.List;

@XmlRootElement(name = "games")
public class GamesXmlWrapper {
    private List<GameXmlDTO> games;

    @XmlElement(name = "game")
    public void setGames(List<GameXmlDTO> games) { this.games = games; }

    public List<GameXmlDTO> getGames() { return games; }
}
