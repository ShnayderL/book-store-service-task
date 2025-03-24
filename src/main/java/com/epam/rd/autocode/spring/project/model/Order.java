package com.epam.rd.autocode.spring.project.model;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Employee employee;

    private LocalDateTime orderDate;
    private BigDecimal price;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<BookItem> bookItems;
}