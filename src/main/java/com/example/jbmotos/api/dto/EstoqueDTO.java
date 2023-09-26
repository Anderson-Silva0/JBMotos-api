package com.example.jbmotos.api.dto;

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
public class EstoqueDTO {

    private Integer id;

    @NotNull(message = "O campo Estoque Mínimo é obrigatório.")
    private Integer estoqueMinimo;

    @NotNull(message = "O campo Estoque Máximo é obrigatório.")
    private Integer estoqueMaximo;

    @NotNull(message = "O campo Quantidade é obrigatório.")
    @Min(value = 0, message = "A Quantidade deve ser maior ou igual a 0.")
    private Integer quantidade;

    private String status;
}
