package com.jbmotos.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbmotos.model.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

	boolean existsProdutoByEstoqueId(Integer idEstoque);
}
