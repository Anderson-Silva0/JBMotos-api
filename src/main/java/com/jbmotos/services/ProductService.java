package com.jbmotos.services;

import java.math.BigDecimal;
import java.util.List;

import com.jbmotos.api.dto.ProductDTO;
import com.jbmotos.model.entity.Product;
import com.jbmotos.model.enums.Situation;

public interface ProductService {

    Product saveProduct(ProductDTO productDTO);

    List<Product> findAllProducts();

    Product findProductById(Integer id);

    List<Product> filterProduct(ProductDTO productDTO);

    Situation toggleProductStatus(Integer productId);

    Product updateProduct(ProductDTO productDTO);

    void deleteProduct(Integer id);

    BigDecimal calculateProductProfit(Integer productId);

    void existsProductById(Integer id);

    boolean existsProductByStockId(Integer stockId);
}
