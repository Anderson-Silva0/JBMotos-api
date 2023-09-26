package com.example.jbmotos.services.impl;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.jbmotos.api.dto.ProdutoDTO;
import com.example.jbmotos.model.entity.Produto;

class ProdutoServiceImplTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void salvarProduto() {
    }

    @Test
    void buscarTodosProdutos() {
    }

    @Test
    void buscarProdutoPorId() {
    }

    @Test
    void atualizarProduto() {
    }

    @Test
    void deletarProduto() {
    }

    @Test
    void calcularLucroProduto() {
    }

    @Test
    void validarEstoqueParaAtualizar() {
    }

    @Test
    void verificaSeProdutoExiste() {
    }

    @Test
    void existeProdutoPorIdEstoque() {
    }

    public static Produto getProduto() {
        return Produto.builder()
                .id(1)
                .nome("Pneu")
                .precoCusto(BigDecimal.valueOf(100.00))
                .precoVenda(BigDecimal.valueOf(150.00))
                .marca("Vipal")
                .estoque(null)
                .fornecedor(null)
                .build();
    }

    public static ProdutoDTO getProdutoDTO() {
        return ProdutoDTO.builder()
                .id(1)
                .nome("Pneu")
                .precoCusto(BigDecimal.valueOf(100.00))
                .precoVenda(BigDecimal.valueOf(150.00))
                .marca("Vipal")
                .idEstoque(1)
                .cnpjFornecedor("00.000.000/0001-00")
                .build();
    }
}