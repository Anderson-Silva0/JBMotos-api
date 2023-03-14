package com.example.jbmotos.api.dto;

import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.model.entity.Produto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdutoPedidoDTO {

    private Integer id;

    @NotNull(message = "Informe o Id do Pedido.")
    private Pedido pedido;

    @NotNull(message = "Informe o Id do Produto.")
    private Produto produto;

    @NotNull(message = "Informe a Quantidade do Produto.")
    private Integer quantidade;

    @NotNull(message = "Informe o Valor do Produto.")
    private BigDecimal valor;
}
