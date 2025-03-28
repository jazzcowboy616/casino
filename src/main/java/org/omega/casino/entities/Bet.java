package org.omega.casino.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
@Table(name = "t_bets")
public class Bet {
    private static final long serialVersionUID = 7419229779731522702L;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "round_id")
    private Round round;

    @Column(nullable = false, columnDefinition = "DECIMAL(8,2)")
    private BigDecimal amount;

    @Column
    private Boolean win;

    @ColumnDefault("0.00")
    @Column(columnDefinition = "DECIMAL(8,2)")
    private BigDecimal winAmount;

    @Column(nullable = false)
    private LocalDateTime betAt;

}
