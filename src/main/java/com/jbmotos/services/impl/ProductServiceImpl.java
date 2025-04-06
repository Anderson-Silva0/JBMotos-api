package com.jbmotos.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.ProductDTO;
import com.jbmotos.model.entity.Stock;
import com.jbmotos.model.entity.Supplier;
import com.jbmotos.model.entity.Product;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.model.repositories.ProductRepository;
import com.jbmotos.services.StockService;
import com.jbmotos.services.SupplierService;
import com.jbmotos.services.ProductService;
import com.jbmotos.services.exception.ObjectNotFoundException;
import com.jbmotos.services.exception.BusinessRuleException;

@Service
public class ProductServiceImpl implements ProductService {

	private static final String PRODUCT_NOT_FOUND_MSG = "Produto não encontrado para o Id informado.";

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private StockService stockService;

	@Autowired
	private SupplierService supplierService;

	@Autowired
	private ModelMapper mapper;

	@Override
	@Transactional
	public Product saveProduct(ProductDTO productDTO) {
		this.stockService.checkStockUsage(productDTO.getStockId());
		Product product = this.mapper.map(productDTO, Product.class);
		product.setProductStatus(Situation.ACTIVE);

		Stock stock = this.stockService.findStockById(productDTO.getStockId());
		product.setStock(stock);

		Supplier supplier = this.supplierService.findSupplierByCnpj(productDTO.getSupplierCnpj());
		product.setSupplier(supplier);

		return this.productRepository.save(product);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Product> findAllProducts() {
		return this.productRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Product findProductById(Integer id) {
		return this.productRepository.findById(id)
				.orElseThrow(() -> new ObjectNotFoundException(PRODUCT_NOT_FOUND_MSG));
	}

	@Override
	public List<Product> filterProduct(ProductDTO productDTO) {
		Example<Product> example = Example.of(this.mapper.map(productDTO, Product.class),
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

		return this.productRepository.findAll(example, sort);
	}

	@Override
	@Transactional
	public Situation toggleProductStatus(Integer productId) {
		Product product = findProductById(productId);
		if (product.getProductStatus().equals(Situation.ACTIVE)) {
			product.setProductStatus(Situation.INACTIVE);
		} else if (product.getProductStatus().equals(Situation.INACTIVE)) {
			product.setProductStatus(Situation.ACTIVE);
		}
		this.productRepository.save(product);
		return product.getProductStatus();
	}

	@Override
	@Transactional
	public Product updateProduct(ProductDTO productDTO) {
		Product product = this.mapper.map(productDTO, Product.class);
		this.validateStockToUpdate(productDTO);

		LocalDateTime dateTime = findProductById(productDTO.getId()).getCreatedAt();
		product.setCreatedAt(dateTime);

		Stock stock = this.stockService.findStockById(productDTO.getStockId());
		product.setStock(stock);

		Supplier supplier = this.supplierService.findSupplierByCnpj(productDTO.getSupplierCnpj());
		product.setSupplier(supplier);

		return this.productRepository.save(product);
	}

	@Override
	@Transactional
	public void deleteProduct(Integer id) {
		this.existsProductById(id);
		this.productRepository.deleteById(id);
	}

	@Override
	@Transactional
	public BigDecimal calculateProductProfit(Integer productId) {
		Product product = findProductById(productId);
		return product.getSalePrice().subtract(product.getCostPrice());
	}

	public void validateStockToUpdate(ProductDTO productDTO) {
		this.filterProductsByDifferentId(productDTO).forEach(filteredProduct -> {
			if (productDTO.getStockId().equals(filteredProduct.getStock().getId())) {
				throw new BusinessRuleException("Erro ao tentar Atualizar, o Estoque já pertence a um Produto.");
			}
		});
	}

	private List<Product> filterProductsByDifferentId(ProductDTO productDTO) {
		return this.findAllProducts().stream().filter(product -> (!productDTO.getId().equals(product.getId())))
				.collect(Collectors.toList());
	}

	@Override
	public void existsProductById(Integer id) {
		if (!this.productRepository.existsById(id)) {
			throw new ObjectNotFoundException(PRODUCT_NOT_FOUND_MSG);
		}
	}

	@Override
	public boolean existsProductByStockId(Integer stockId) {
		return this.productRepository.existsProductByStockId(stockId);
	}
}
