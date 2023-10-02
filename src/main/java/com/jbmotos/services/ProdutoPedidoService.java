package com.jbmotos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.ProdutoPedidoDTO;
import com.jbmotos.model.entity.ProdutoPedido;

public interface ProdutoPedidoService {

    ProdutoPedido salvarProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO);

    List<ProdutoPedido> buscarTodosProdutoPedido();

    Optional<ProdutoPedido> buscarProdutoPedidoPorId(Integer id);

    ProdutoPedido atualizarProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO);

    void deletarProdutoPedidoPorId(Integer id);

    @Transactional(readOnly = true)
    List<ProdutoPedido> buscarProdutoPedidoPorIdPedido(Integer idPedido);

    void validarProdutoPedido(Integer id);

    void atualizarQtdEstoqueParaDeletar(Integer id);
}
