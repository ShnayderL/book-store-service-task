package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.*;
import lombok.*;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    private String name;
}