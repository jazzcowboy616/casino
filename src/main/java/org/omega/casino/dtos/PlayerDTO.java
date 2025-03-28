package org.omega.casino.dtos;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class PlayerDTO {
    private static final long serialVersionUID = 7419229779731522702L;

    private Long id;

    private String name;

    private String username;

    @JsonIgnore
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private BigDecimal balance;

    private Boolean online;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mi:ss")
    private String lastLoginTime;

    public PlayerDTO(Long id, String username, String password, String name, LocalDate birthDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
    }

    public PlayerDTO(Long id, String username, String password, String name, LocalDate birthDate, BigDecimal balance, Boolean online) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.balance = balance;
        this.online = online;
    }

}
