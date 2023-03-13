package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.PedidoDTO;
import com.example.jbmotos.model.entity.Pedido;

import java.util.List;
import java.util.Optional;

public interface PedidoService {
    
    Pedido salvarPedido(PedidoDTO PedidoDTO);

    List<Pedido> buscarTodosPedidos();

    Optional<Pedido> buscarpedidoPorId(Integer id);

    pedido atualizarpedido(pedidoDTO pedidoDTO);

    void deletarpedido(Integer id);

    void verificaSepedidoExiste(Integer id);
}
