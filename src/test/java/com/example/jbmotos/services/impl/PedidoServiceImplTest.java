package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.PedidoDTO;
import com.example.jbmotos.model.entity.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PedidoServiceImplTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void salvarPedido() {
    }

    @Test
    void buscarTodosPedidos() {
    }

    @Test
    void buscarPedidoPorId() {
    }

    @Test
    void atualizarPedido() {
    }

    @Test
    void deletarPedido() {
    }

    @Test
    void calcularLucroDoPedido() {
    }

    @Test
    void validarPedido() {
    }

    @Test
    void valorTotalDoPedido() {
    }

    @Test
    void buscarProdutosDoPedido() {
    }

    public static Pedido getPedido() {
        return Pedido.builder()
                .id(1)
                .cliente(null)
                .funcionario(null)
                .dataHoraCadastro(LocalDateTime.now())
                .observacao("")
                .formaDePagamento("PIX")
                .build();
    }

    public static PedidoDTO getPedidoDTO() {
        return PedidoDTO.builder()
                .id(1)
                .cpfCliente("123.456.789-10")
                .cpfFuncionario("109.876.543-21")
                .dataHoraCadastro(LocalDateTime.now())
                .observacao("")
                .formaDePagamento("PIX")
                .build();
    }
}