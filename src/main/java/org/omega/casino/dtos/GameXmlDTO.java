package org.omega.casino.dtos;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

import java.math.BigDecimal;

@Data
@XmlRootElement(name = "game")
public class GameXmlDTO {
    private String name;
    private String description;
    private double winRate;
    private double winMultiplier;
    private BigDecimal minBet;
    private BigDecimal maxBet;

    @XmlElement
    public void setName(String name) { this.name = name; }

    @XmlElement
    public void setDescription(String description) { this.description = description; }

    @XmlElement
    public void setWinRate(double winRate) { this.winRate = winRate; }

    @XmlElement
    public void setWinMultiplier(double winMultiplier) { this.winMultiplier = winMultiplier; }

    @XmlElement
    public void setMinBet(BigDecimal minBet) { this.minBet = minBet; }

    @XmlElement
    public void setMaxBet(BigDecimal maxBet) { this.maxBet = maxBet; }

}
