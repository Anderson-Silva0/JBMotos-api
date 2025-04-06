package com.jbmotos.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ProductsOfSaleDTO {

    private Integer id;

    @NotNull(message = "O Id da Venda não pode ser nulo.")
    private Integer saleId;

    private ProductDTO product;

    @NotNull(message = "Informe a Quantidade do Produto.")
    @Min(value = 1, message = "A Quantidade deve ser maior ou igual a 1.")
    private Integer quantity;

    private BigDecimal unitValue;

    private BigDecimal totalValue;
}
