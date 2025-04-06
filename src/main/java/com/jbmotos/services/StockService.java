package com.jbmotos.services;

import java.math.BigDecimal;
import java.util.List;

import com.jbmotos.api.dto.StockDTO;
import com.jbmotos.model.entity.Stock;

public interface StockService {

    Stock saveStock(StockDTO stockDTO);

    List<Stock> findAllStocks();

    Stock findStockById(Integer id);

    Stock updateStock(StockDTO stockDTO);

    void deleteStockById(Integer id);

    Integer getProductQuantity(Integer productId);

    void addStockQuantity(Integer productId, Integer quantity);

    BigDecimal calculateTotalStockCost();

    BigDecimal calculateStockSalesPotential();

    void validateStock(Integer id);

    void checkStockUsage(Integer id);
}
