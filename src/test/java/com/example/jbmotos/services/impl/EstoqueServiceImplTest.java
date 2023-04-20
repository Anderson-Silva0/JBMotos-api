package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.EstoqueDTO;
import com.example.jbmotos.model.entity.Estoque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstoqueServiceImplTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void salvarEstoque() {
    }

    @Test
    void buscarTodosEstoques() {
    }

    @Test
    void buscarEstoquePorId() {
    }

    @Test
    void atualizarEstoque() {
    }

    @Test
    void deletarEstoquePorId() {
    }

    @Test
    void obterQuantidadeDoProduto() {
    }

    @Test
    void adicionarQuantidadeAoEstoque() {
    }

    @Test
    void calcularValorTotalEstoque() {
    }

    @Test
    void validarEstoque() {
    }

    @Test
    void verificarUsoEstoque() {
    }

    public static Estoque getEstoque() {
        return Estoque.builder()
                .id(1)
                .produto(null)
                .estoqueMinimo(5)
                .estoqueMaximo(10)
                .quantidade(5)
                .status(null)
                .build();
    }

    public static EstoqueDTO getEstoqueDTO() {
        return EstoqueDTO.builder()
                .id(1)
                .estoqueMinimo(5)
                .estoqueMaximo(10)
                .quantidade(5)
                .status(null)
                .build();
    }
}