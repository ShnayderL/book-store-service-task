package com.epam.rd.autocode.spring.project.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

// Client.java
@Entity
@Table(name = "CLIENTS")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Client extends User {
    private BigDecimal balance;

    public Client(Long id, String email, String password, String name,
                  BigDecimal balance) {
        super(id, email, password, name);
        this.balance = balance;
    }
}