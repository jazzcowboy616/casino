package org.omega.casino.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterPlayerDTO implements Cloneable {
    @NotNull
    private String username;
    @NotNull
    @Size(min = 5, max = 16)
    private String password;
    @NotNull
    private String repeatPassword;
    @NotNull
    private String name;
    @NotNull
    private String birthDate;

    @AssertTrue(message = "{birth.valid}")
    public boolean isBirthDateValid() {
        if (birthDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; // or DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate bDate = LocalDate.parse(birthDate, formatter);
        return Period.between(bDate, today).getYears() >= 18;
    }


    @Override
    public String toString() {
        return "RegisterPlayerDto{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", birthDate='" + birthDate + '\'' +
                '}';
    }

    @Override
    public RegisterPlayerDTO clone() {
        try {
            return (RegisterPlayerDTO) super.clone(); // 浅拷贝
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
