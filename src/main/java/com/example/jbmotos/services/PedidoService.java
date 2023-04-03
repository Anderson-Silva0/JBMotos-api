package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.PedidoDTO;
import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.model.entity.Produto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PedidoService {
    
    Pedido salvarPedido(PedidoDTO pedidoDTO);

    List<Pedido> buscarTodosPedidos();

    Optional<Pedido> buscarPedidoPorId(Integer id);

    Pedido atualizarPedido(PedidoDTO pedidoDTO);

    void deletarPedido(Integer id);

    BigDecimal calcularLucroDoPedido(Integer idPedido);

    void validarPedido(Integer id);

    BigDecimal valorTotalDoPedido(Integer idPedido);

    List<Produto> buscarProdutosDoPedido(Integer idPedido);
}
