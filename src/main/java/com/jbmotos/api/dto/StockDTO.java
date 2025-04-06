package com.jbmotos.api.dto;

import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class StockDTO {

    private Integer id;

    @NotNull(message = "O campo Estoque Mínimo é obrigatório.")
    private Integer minStock;

    @NotNull(message = "O campo Estoque Máximo é obrigatório.")
    private Integer maxStock;

    @NotNull(message = "O campo Quantidade é obrigatório.")
    @Min(value = 0, message = "A Quantidade deve ser maior ou igual a 0.")
    private Integer quantity;

    private String status;
}
