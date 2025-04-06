package com.jbmotos.services.impl;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jbmotos.api.dto.ProductDTO;
import com.jbmotos.model.entity.Product;

class ProductServiceImplTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void saveProduct() {
    }

    @Test
    void findAllProducts() {
    }

    @Test
    void findProductById() {
    }

    @Test
    void updateProduct() {
    }

    @Test
    void deleteProduct() {
    }

    @Test
    void calculateProductProfit() {
    }

    @Test
    void validateStockToUpdate() {
    }

    @Test
    void existsProductById() {
    }

    @Test
    void existsProductByStockId() {
    }

    public static Product getProduto() {
        return Product.builder()
                .id(1)
                .name("Pneu")
                .costPrice(BigDecimal.valueOf(100.00))
                .salePrice(BigDecimal.valueOf(150.00))
                .brand("Vipal")
                .stock(null)
                .supplier(null)
                .build();
    }

    public static ProductDTO getProdutoDTO() {
        return ProductDTO.builder()
                .id(1)
                .name("Pneu")
                .costPrice(BigDecimal.valueOf(100.00))
                .salePrice(BigDecimal.valueOf(150.00))
                .brand("Vipal")
                .stockId(1)
                .supplierCnpj("00.000.000/0001-00")
                .build();
    }
}