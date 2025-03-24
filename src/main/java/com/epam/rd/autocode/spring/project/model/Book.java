package com.epam.rd.autocode.spring.project.model;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String genre;

    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    private BigDecimal price;
    private LocalDate publicationDate;
    private String author;
    private Integer pages;
    private String characteristics;
    private String description;

    @Enumerated(EnumType.STRING)
    private Language language;
}