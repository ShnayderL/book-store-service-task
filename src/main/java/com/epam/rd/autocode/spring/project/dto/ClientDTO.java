package com.epam.rd.autocode.spring.project.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ClientDTO {
    private String email;
    private String password;
    private String name;
    private BigDecimal balance;
}
