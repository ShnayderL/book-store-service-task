package com.epam.rd.autocode.spring.project.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Table(name = "EMPLOYEES")
@Data
@NoArgsConstructor
public class Employee extends User {
    private  LocalDate birthDate;
    private String phone;

    public Employee(Long id, String email, String password, String name,
                    LocalDate birthDate, String phone) {
        super(id, email, password, name);
        this.birthDate = birthDate;
        this.phone = phone;
    }
}


