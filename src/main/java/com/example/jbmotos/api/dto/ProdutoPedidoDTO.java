package com.example.jbmotos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdutoPedidoDTO {

    private Integer id;

    @NotNull(message = "Informe o Id do Pedido.")
    private Integer idPedido;

    @NotNull(message = "Informe o Id do Produto.")
    private Integer idProduto;

    @NotNull(message = "Informe a Quantidade do Produto.")
    @Min(value = 1, message = "A Quantidade deve ser maior ou igual a 1.")
    private Integer quantidade;

    private BigDecimal valorUnidade;

    private BigDecimal valorTotal;
}
