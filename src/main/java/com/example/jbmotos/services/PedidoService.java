package com.example.jbmotos.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.example.jbmotos.api.dto.PedidoDTO;
import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.model.entity.Produto;

public interface PedidoService {

    Pedido salvarPedido(PedidoDTO pedidoDTO);

    List<Pedido> buscarTodosPedidos();

    Optional<Pedido> buscarPedidoPorId(Integer id);

    List<Pedido> filtrarPedido(PedidoDTO pedidoDTO);

    Pedido atualizarPedido(PedidoDTO pedidoDTO);

    void deletarPedido(Integer id);

    BigDecimal calcularLucroDoPedido(Integer idPedido);

    void validarPedido(Integer id);

    BigDecimal valorTotalDoPedido(Integer idPedido);

    List<Produto> buscarProdutosDoPedido(Integer idPedido);
}
