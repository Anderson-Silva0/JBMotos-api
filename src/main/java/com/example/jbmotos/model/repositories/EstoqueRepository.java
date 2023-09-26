package com.example.jbmotos.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jbmotos.model.entity.Estoque;

public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

}
