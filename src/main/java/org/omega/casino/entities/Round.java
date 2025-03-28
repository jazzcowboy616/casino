package org.omega.casino.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
@Table(name = "t_rounds")
public class Round {
    private static final long serialVersionUID = 7419229779731522702L;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "is_settled", nullable = false)
    private Boolean settled = false;

    public Round(Long id) {
        this.id = id;
    }
}
