package com.jbmotos.services.impl;

import java.math.BigDecimal;
import java.util.List;

import com.jbmotos.api.dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.StockDTO;
import com.jbmotos.api.dto.ProductsOfSaleDTO;
import com.jbmotos.model.entity.Stock;
import com.jbmotos.model.entity.Product;
import com.jbmotos.model.entity.ProductsOfSale;
import com.jbmotos.model.entity.Sale;
import com.jbmotos.model.enums.StockStatus;
import com.jbmotos.model.repositories.ProductsOfSaleRepository;
import com.jbmotos.services.StockService;
import com.jbmotos.services.ProductService;
import com.jbmotos.services.ProductsOfSaleService;
import com.jbmotos.services.SaleService;
import com.jbmotos.services.exception.ObjectNotFoundException;
import com.jbmotos.services.exception.BusinessRuleException;

@Service
public class ProductsOfSaleServiceImpl implements ProductsOfSaleService {

	private static final String PRODUCT_OF_SALE_NOT_FOUND_MSG = "Produto da Venda não encontrado para o Id informado.";

	private static final String ERROR_SAVE_PRODUCT_OF_SALE_MSG = "Não é possível realizar a Venda pois a "
			+ "quantidade solicitada do produto é maior do que a quantidade disponível em estoque.";

	private static final String ERROR_UPDATE_PRODUCT_OF_SALE_MSG = "Não é possível Atualizar a Venda pois a "
			+ "quantidade solicitada do Produto é maior do que a quantidade disponível em estoque.";

	private static final String ERROR_UPDATE_NEW_PRODUCT_MSG = "Não é possível Atualizar a Venda pois a quantidade "
			+ "solicitada do novo Produto é maior do que a quantidade disponível em estoque.";

	@Autowired
	@Lazy
	private SaleService saleService;

	@Autowired
	private ProductService productService;

	@Autowired
	private StockService stockService;

	@Autowired
	private ProductsOfSaleRepository productsOfSaleRepository;

	@Autowired
	private ModelMapper mapper;

	@Override
	@Transactional
	public ProductsOfSale saveProductsOfSale(ProductsOfSaleDTO productsOfSaleDTO) {
		ProductsOfSale productsOfSale = this.getProductsOfSaleToSave(productsOfSaleDTO);
		return this.productsOfSaleRepository.save(productsOfSale);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductsOfSale> findAllProductsOfSale() {
		return this.productsOfSaleRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public ProductsOfSale findProductsOfSaleById(Integer id) {
		return this.productsOfSaleRepository.findById(id)
				.orElseThrow(() -> new ObjectNotFoundException(PRODUCT_OF_SALE_NOT_FOUND_MSG));
	}

	@Override
	@Transactional
	public ProductsOfSale updateProductsOfSale(ProductsOfSaleDTO productsOfSaleDTO) {
		this.validateProductsOfSaleById(productsOfSaleDTO.getId());
		ProductsOfSale productsOfSale = this.getProductsOfSaleToUpdate(productsOfSaleDTO);
		return this.productsOfSaleRepository.save(productsOfSale);
	}

	@Override
	@Transactional
	public void deleteProductsOfSaleById(Integer id) {
		this.validateProductsOfSaleById(id);
		this.updateStockQuantityToDelete(id);
		this.productsOfSaleRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductsOfSale> findProductsOfSaleBySaleId(Integer saleId) {
		return this.productsOfSaleRepository.findProductsOfSaleBySaleId(saleId);
	}

	@Override
	@Transactional(readOnly = true)
	public void validateProductsOfSaleById(Integer id) {
		if (!this.productsOfSaleRepository.existsById(id)) {
			throw new ObjectNotFoundException(PRODUCT_OF_SALE_NOT_FOUND_MSG);
		}
	}

	private ProductsOfSale getProductsOfSaleToSave(ProductsOfSaleDTO productsOfSaleDTO) {
		ProductsOfSale productsOfSale = this.mapper.map(productsOfSaleDTO, ProductsOfSale.class);

		Sale sale = this.saleService.findSaleById(productsOfSaleDTO.getSaleId());
		productsOfSale.setSale(sale);

		ProductDTO productDTO = productsOfSaleDTO.getProduct();
		Product product = this.productService.findProductById(productDTO.getId());
		productsOfSale.setProduct(product);

		this.checkIfProductAlreadyExistsForSaleToSave(productsOfSale);

		Stock stock = productsOfSale.getProduct().getStock();
		this.validateStockToSave(productsOfSale.getQuantity(), stock.getQuantity(), stock.getStatus());

		productsOfSale.setUnitValue(productsOfSale.getProduct().getSalePrice());
		productsOfSale.setTotalValue(
				productsOfSale.getUnitValue().multiply(BigDecimal.valueOf(productsOfSale.getQuantity())));

		this.changeStockQuantityToSave(productsOfSale);

		return productsOfSale;
	}

	private ProductsOfSale getProductsOfSaleToUpdate(ProductsOfSaleDTO productsOfSaleDTO) {
		ProductsOfSale productsOfSale = findProductsOfSaleById(productsOfSaleDTO.getId());

		this.checkIfProductAlreadyExistsForSaleToUpdate(productsOfSale, productsOfSaleDTO);

		ProductDTO productDTO = productsOfSaleDTO.getProduct();
		Integer produtoDtoId = productDTO.getId();

		if (!produtoDtoId.equals(productsOfSale.getProduct().getId())) {
			this.updateSaleWithNewProduct(productsOfSale, productsOfSaleDTO);
		} else {
			Sale sale = this.saleService.findSaleById(productsOfSaleDTO.getSaleId());
			productsOfSale.setSale(sale);

			Product product = this.productService.findProductById(produtoDtoId);
			productsOfSale.setProduct(product);

			Stock stock = productsOfSale.getProduct().getStock();
			this.validateStockToUpdate(stock.getQuantity(), productsOfSaleDTO.getQuantity(),
					productsOfSale.getQuantity());
			this.changeStockQuantityToUpdate(productsOfSale, productsOfSaleDTO);
		}

		productsOfSale.setUnitValue(productsOfSale.getProduct().getSalePrice());
		productsOfSale.setTotalValue(
				productsOfSale.getUnitValue().multiply(BigDecimal.valueOf(productsOfSaleDTO.getQuantity())));

		return productsOfSale;
	}

	private void updateSaleWithNewProduct(ProductsOfSale productsOfSale, ProductsOfSaleDTO productsOfSaleDTO) {
		Integer productsOfSaleQuantityOld = productsOfSale.getQuantity();
		Integer quantityOldStock = productsOfSale.getProduct().getStock().getQuantity();

		// Devolver a quantidade de estoque do produto antigo, pois mudou de produto.
		Stock stockOldProduct = productsOfSale.getProduct().getStock();
		stockOldProduct.setQuantity(productsOfSaleQuantityOld + quantityOldStock);
		this.stockService.updateStock(this.mapper.map(stockOldProduct, StockDTO.class));

		ProductDTO productDTO = productsOfSaleDTO.getProduct();

		// Abater a quantidade de estoque do novo produto.
		Product newProduct = this.productService.findProductById(productDTO.getId());

		Stock stockNewProduct = newProduct.getStock();

		if (productsOfSaleDTO.getQuantity() > newProduct.getStock().getQuantity()) {
			throw new BusinessRuleException(ERROR_UPDATE_NEW_PRODUCT_MSG);
		}

		stockNewProduct.setQuantity(stockNewProduct.getQuantity() - productsOfSaleDTO.getQuantity());

		Sale sale = this.saleService.findSaleById(productsOfSaleDTO.getSaleId());
		productsOfSale.setSale(sale);

		productsOfSale.setProduct(newProduct);
		productsOfSale.setQuantity(productsOfSaleDTO.getQuantity());

		this.stockService.updateStock(this.mapper.map(stockNewProduct, StockDTO.class));
	}

	private void changeStockQuantityToSave(ProductsOfSale productsOfSale) {
		Stock stock = productsOfSale.getProduct().getStock();
		stock.setQuantity(stock.getQuantity() - productsOfSale.getQuantity());
		this.stockService.updateStock(this.mapper.map(stock, StockDTO.class));
	}

	private void changeStockQuantityToUpdate(ProductsOfSale productsOfSale, ProductsOfSaleDTO productsOfSaleDTO) {
		Stock stock = productsOfSale.getProduct().getStock();

		Integer currentStockQuantity = stock.getQuantity();
		Integer previousProductQuantity = productsOfSale.getQuantity();
		Integer newProductQuantity = productsOfSaleDTO.getQuantity();

		Integer newStockQuantity = currentStockQuantity + previousProductQuantity - newProductQuantity;
		stock.setQuantity(newStockQuantity);

		productsOfSale.setQuantity(newProductQuantity);

		this.stockService.updateStock(this.mapper.map(stock, StockDTO.class));
	}

	@Transactional(readOnly = true)
	@Override
	public void updateStockQuantityToDelete(Integer id) {
		ProductsOfSale productsOfSale = this.findProductsOfSaleById(id);
		this.stockService.addStockQuantity(productsOfSale.getProduct().getId(), productsOfSale.getQuantity());
	}

	private void validateStockToSave(Integer productQuantity, Integer stockQuantity, StockStatus status) {
		if (status == StockStatus.UNAVAILABLE) {
			throw new BusinessRuleException("Estoque indisponível.");
		} else if (productQuantity > stockQuantity) {
			throw new BusinessRuleException(ERROR_SAVE_PRODUCT_OF_SALE_MSG);
		}
	}

	private void validateStockToUpdate(Integer currentStockQuantity, Integer newProductQuantity,
			Integer previousProductQuantity) {
		if (currentStockQuantity + previousProductQuantity - newProductQuantity < 0) {
			throw new BusinessRuleException(ERROR_UPDATE_PRODUCT_OF_SALE_MSG);
		}
	}

	private void checkIfProductAlreadyExistsForSaleToSave(ProductsOfSale productsOfSale) {
		Sale sale = productsOfSale.getSale();
		Product product = productsOfSale.getProduct();
		if (this.productsOfSaleRepository.existsProductsOfSalesBySaleIdAndProductId(sale.getId(), product.getId())) {
			throw new BusinessRuleException("Erro ao tentar Salvar, Produto já adicionado à Venda.");
		}
	}

	private void checkIfProductAlreadyExistsForSaleToUpdate(ProductsOfSale productsOfSale, ProductsOfSaleDTO dto) {
		this.filterProductSaleByDifferentId(productsOfSale).forEach(filteredProductsOfSale -> {
			ProductDTO productDTO = dto.getProduct();
			if (productDTO.getId().equals(filteredProductsOfSale.getProduct().getId())
					&& dto.getSaleId().equals(filteredProductsOfSale.getSale().getId())) {
				throw new BusinessRuleException("Erro ao tentar Atualizar, Produto já adicionado à Venda.");
			}
		});
	}

	private List<ProductsOfSale> filterProductSaleByDifferentId(ProductsOfSale productsOfSale) {
		return this.productsOfSaleRepository.findByIdNot(productsOfSale.getId());
	}
}
