package com.jbmotos.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbmotos.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	boolean existsProductByStockId(Integer stockId);
}
