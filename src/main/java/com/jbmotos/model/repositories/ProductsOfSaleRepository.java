package com.jbmotos.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbmotos.model.entity.ProductsOfSale;

public interface ProductsOfSaleRepository extends JpaRepository<ProductsOfSale, Integer> {

    List<ProductsOfSale> findProductsOfSaleBySaleId(Integer id);

    boolean existsProductsOfSalesBySaleIdAndProductId(Integer saleId, Integer productId);

    List<ProductsOfSale> findByIdNot(Integer id);
}
