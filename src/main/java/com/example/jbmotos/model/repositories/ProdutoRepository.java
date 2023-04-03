package com.example.jbmotos.model.repositories;

import com.example.jbmotos.model.entity.Produto;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    boolean existsProdutoByEstoqueId(Integer idEstoque);
}
