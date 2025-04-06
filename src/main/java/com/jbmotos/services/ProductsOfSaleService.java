package com.jbmotos.services;

import java.util.List;

import com.jbmotos.api.dto.ProductsOfSaleDTO;
import com.jbmotos.model.entity.ProductsOfSale;

public interface ProductsOfSaleService {

    ProductsOfSale saveProductsOfSale(ProductsOfSaleDTO productsOfSaleDTO);

    List<ProductsOfSale> findAllProductsOfSale();

    ProductsOfSale findProductsOfSaleById(Integer id);

    ProductsOfSale updateProductsOfSale(ProductsOfSaleDTO productsOfSaleDTO);

    void deleteProductsOfSaleById(Integer id);

    List<ProductsOfSale> findProductsOfSaleBySaleId(Integer saleId);

    void validateProductsOfSaleById(Integer id);

    void updateStockQuantityToDelete(Integer id);
}
