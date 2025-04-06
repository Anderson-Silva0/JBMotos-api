package com.jbmotos.services;

import java.math.BigDecimal;
import java.util.List;

import com.jbmotos.api.dto.SaleDTO;
import com.jbmotos.model.entity.Product;
import com.jbmotos.model.entity.Sale;

public interface SaleService {

    Sale saveSale(SaleDTO saleDTO);

    List<Sale> findAllSales();

    Sale findSaleById(Integer id);

    List<Sale> filterSale(SaleDTO saleDTO);

    Sale updateSale(SaleDTO saleDTO);

    void deleteSaleById(Integer id);

    BigDecimal calculateSaleProfit(Integer saleId);

    void validateSale(Integer id);

    List<Product> findProductsOfSaleBySaleId(Integer saleId);
}
