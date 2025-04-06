package com.jbmotos.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbmotos.model.entity.Stock;

public interface StockRepository extends JpaRepository<Stock, Integer> {

}
