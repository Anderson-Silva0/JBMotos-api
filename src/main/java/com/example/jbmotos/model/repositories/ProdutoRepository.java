package com.example.jbmotos.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jbmotos.model.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

	boolean existsProdutoByEstoqueId(Integer idEstoque);
}
