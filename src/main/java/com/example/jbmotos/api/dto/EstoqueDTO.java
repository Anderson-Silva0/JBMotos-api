package com.example.jbmotos.api.dto;

import com.example.jbmotos.model.entity.Produto;
import com.example.jbmotos.model.enums.StatusEstoque;

import javax.validation.constraints.NotNull;

public class EstoqueDTO {

    private Integer id;

    private Produto produto;

    @NotNull(message = "O campo Estoque Mínimo é obrigatório.")
    private Integer estoqueMinimo;

    @NotNull(message = "O campo Estoque Máximo é obrigatório.")
    private Integer estoqueMaximo;

    @NotNull(message = "O campo Quantidade é obrigatório.")
    private Integer quantidade;

    @NotNull(message = "O campo Status é obrigatório.")
    private StatusEstoque status;
}
