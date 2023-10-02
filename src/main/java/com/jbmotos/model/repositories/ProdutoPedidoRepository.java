package com.jbmotos.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbmotos.model.entity.ProdutoPedido;

public interface ProdutoPedidoRepository extends JpaRepository<ProdutoPedido, Integer> {

    List<ProdutoPedido> findProdutoPedidoByPedidoId(Integer id);

    boolean existsProdutoPedidosByPedidoIdAndProdutoId(Integer idPedido, Integer idProduto);

    List<ProdutoPedido> findByIdNot(Integer id);
}
