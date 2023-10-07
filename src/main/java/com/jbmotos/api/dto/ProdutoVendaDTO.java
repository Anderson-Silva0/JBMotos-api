package com.jbmotos.api.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdutoVendaDTO {

    private Integer id;

    @NotNull(message = "O Id da Venda não pode ser nulo.")
    private Integer idVenda;

    @NotNull(message = "O Id do Produto não pode ser nulo.")
    private Integer idProduto;

    @NotNull(message = "Informe a Quantidade do Produto.")
    @Min(value = 1, message = "A Quantidade deve ser maior ou igual a 1.")
    private Integer quantidade;

    private BigDecimal valorUnidade;

    private BigDecimal valorTotal;
}
