package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.ProdutoPedidoDTO;
import com.example.jbmotos.model.entity.ProdutoPedido;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProdutoPedidoService {

    ProdutoPedido salvarProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO);

    List<ProdutoPedido> buscarTodosProdutoPedido();

    Optional<ProdutoPedido> buscarProdutoPedidoPorId(Integer id);

    ProdutoPedido atualizarProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO);

    void deletarProdutoPedidoPorId(Integer id);

    @Transactional(readOnly = true)
    List<ProdutoPedido> buscarProdutoPedidoPorIdPedido(Integer idPedido);

    void validarProdutoPedido(Integer id);
}
