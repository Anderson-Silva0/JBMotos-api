package com.example.jbmotos.model.repositories;

import com.example.jbmotos.model.entity.ProdutoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoPedidoRepository extends JpaRepository<ProdutoPedido, Integer> {

}
