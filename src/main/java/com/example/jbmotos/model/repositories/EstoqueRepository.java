package com.example.jbmotos.model.repositories;

import com.example.jbmotos.model.entity.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

}
