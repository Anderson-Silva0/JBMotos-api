package com.jbmotos.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.jbmotos.model.entity.Stock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.StockDTO;
import com.jbmotos.model.entity.Product;
import com.jbmotos.model.enums.StockStatus;
import com.jbmotos.model.repositories.StockRepository;
import com.jbmotos.services.StockService;
import com.jbmotos.services.ProductService;
import com.jbmotos.services.exception.ObjectNotFoundException;
import com.jbmotos.services.exception.BusinessRuleException;

@Service
public class StockServiceImpl implements StockService {

	private static final String STOCK_NOT_FOUND_MSG = "Estoque não encontrado para o Id informado.";

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    @Lazy
    private ProductService productService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Stock saveStock(StockDTO stockDTO) {
        Stock stock = this.mapper.map(stockDTO, Stock.class);
        stock.setStatus(this.getStockStatus(stock));
        return this.stockRepository.save(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stock> findAllStocks() {
        return this.stockRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Stock findStockById(Integer id) {
        return this.stockRepository.findById(id)
        		.orElseThrow(() -> new ObjectNotFoundException(STOCK_NOT_FOUND_MSG));
    }

    @Override
    @Transactional
    public Stock updateStock(StockDTO stockDTO) {
        this.validateStock(stockDTO.getId());
        Stock stock = this.mapper.map(stockDTO, Stock.class);
        stock.setStatus(this.getStockStatus(stock));
        return this.stockRepository.save(stock);
    }

    @Override
    @Transactional
    public void deleteStockById(Integer id) {
        this.validateStock(id);
        this.checkStockUsage(id);
        this.stockRepository.deleteById(id);
    }

	@Override
	public Integer getProductQuantity(Integer productId) {
		Product product = this.productService.findProductById(productId);
        Integer productQuantity = null;
        if (Optional.ofNullable(product).isPresent()) {
            Stock productStock = product.getStock();
            if (Optional.ofNullable(productStock).isPresent()) {
                productQuantity = productStock.getQuantity();
            }
        }
		return productQuantity;
	}

	@Override
	@Transactional
	public void addStockQuantity(Integer productId, Integer quantity) {
		Product product = this.productService.findProductById(productId);
        if (Optional.ofNullable(product).isPresent()) {
            Stock productStock = product.getStock();
            if (Optional.ofNullable(productStock).isPresent()) {
                productStock.setQuantity(productStock.getQuantity() + quantity);
                this.updateStock(this.mapper.map(productStock, StockDTO.class));
            }
        }
	}

	@Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalStockCost() {
        return findAllStocks().stream()
                .map(stock -> stock.getProduct().getCostPrice()
                        .multiply(BigDecimal.valueOf(stock.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateStockSalesPotential() {
        return findAllStocks().stream()
                .map(stock -> stock.getProduct().getSalePrice()
                        .multiply(BigDecimal.valueOf(stock.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void validateStock(Integer id) {
        if (!this.stockRepository.existsById(id)) {
            throw new ObjectNotFoundException(STOCK_NOT_FOUND_MSG);
        }
    }

    @Override
    public void checkStockUsage(Integer id) {
        if (this.productService.existsProductByStockId(id)) {
            throw new BusinessRuleException("O Estoque pertence a um Produto.");
        }
    }

    private StockStatus getStockStatus(Stock stock) {
        if (stock.getQuantity() > stock.getMaxStock()) {
            return StockStatus.HIGH_STOCK;
        } else if (stock.getQuantity() < stock.getMinStock() && stock.getQuantity() > 0) {
            return StockStatus.LOW_STOCK;
        } else if (stock.getQuantity() == 0) {
            return StockStatus.UNAVAILABLE;
        }
        return StockStatus.AVAILABLE;
    }
}
