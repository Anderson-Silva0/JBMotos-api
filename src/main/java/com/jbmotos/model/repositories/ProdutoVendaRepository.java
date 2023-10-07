package com.jbmotos.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbmotos.model.entity.ProdutoVenda;

public interface ProdutoVendaRepository extends JpaRepository<ProdutoVenda, Integer> {

    List<ProdutoVenda> findProdutoVendaByVendaId(Integer id);

    boolean existsProdutoVendasByVendaIdAndProdutoId(Integer idPedido, Integer idProduto);

    List<ProdutoVenda> findByIdNot(Integer id);
}
