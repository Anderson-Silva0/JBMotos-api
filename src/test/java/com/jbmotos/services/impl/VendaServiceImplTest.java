package com.jbmotos.services.impl;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jbmotos.api.dto.ClienteDTO;
import com.jbmotos.api.dto.FuncionarioDTO;
import com.jbmotos.api.dto.VendaDTO;
import com.jbmotos.model.entity.Venda;

class VendaServiceImplTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void salvarVenda() {
    }

    @Test
    void buscarTodasVendas() {
    }

    @Test
    void buscarVendaPorId() {
    }

    @Test
    void atualizarVenda() {
    }

    @Test
    void deletarVenda() {
    }

    @Test
    void calcularLucroDaVenda() {
    }

    @Test
    void validarVenda() {
    }

    @Test
    void valorTotalDaVenda() {
    }

    @Test
    void buscarProdutosDaVenda() {
    }

    public static Venda getVenda() {
        return Venda.builder()
                .id(1)
                .cliente(null)
                .funcionario(null)
                .dataHoraCadastro(LocalDateTime.now())
                .observacao("")
                .formaDePagamento("PIX")
                .build();
    }

    public static VendaDTO getVendaDTO() {
        return VendaDTO.builder()
                .id(1)
                .cliente(ClienteDTO.builder().cpf("123.456.789-10").build())
                .funcionario(FuncionarioDTO.builder().cpf("109.876.543-21").build())
                .dataHoraCadastro(LocalDateTime.now())
                .observacao("")
                .formaDePagamento("PIX")
                .build();
    }
}