package com.epam.rd.autocode.spring.project.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookItemDTO {
    private BookDTO book;
    private Integer quantity;
}