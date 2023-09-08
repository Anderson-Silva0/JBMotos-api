package com.example.jbmotos.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jbmotos.model.entity.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
	boolean existsProdutoByEstoqueId(Integer idEstoque);
}
