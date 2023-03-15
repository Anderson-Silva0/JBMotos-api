package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.ProdutoPedidoDTO;
import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.model.entity.ProdutoPedido;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProdutoPedidoService {

    ProdutoPedido salvarProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO);

    List<ProdutoPedido> buscarProdutosDoPedido(Integer id);

    BigDecimal valorTotalDoPedido(Integer idPedido);

    Optional<ProdutoPedido> buscarProdutoPedidoPorId(Integer id);

    ProdutoPedido atualizarProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO);

    void deletarProdutoPedido(Integer id);

    void validaProdutoPedido(Integer id);
}
