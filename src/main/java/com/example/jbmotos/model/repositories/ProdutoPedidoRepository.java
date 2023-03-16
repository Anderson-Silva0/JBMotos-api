package com.example.jbmotos.model.repositories;

import com.example.jbmotos.model.entity.ProdutoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoPedidoRepository extends JpaRepository<ProdutoPedido, Integer> {
    List<ProdutoPedido> findProdutoPedidoByPedidoId(Integer id);
}
