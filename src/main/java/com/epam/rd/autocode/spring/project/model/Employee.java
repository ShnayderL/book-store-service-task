package com.epam.rd.autocode.spring.project.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
@Entity
@Data
@NoArgsConstructor
@Getter
@Setter
public class Employee extends User {
    private String phone;
    private LocalDate birthDate;

    public Employee(Long id, String email, String password, String name, String phone, LocalDate birthDate) {
        super(id, email, password, name);
        this.phone = phone;
        this.birthDate = birthDate;
    }
}


