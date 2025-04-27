package com.jbmotos.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jbmotos.api.dto.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.model.entity.Customer;
import com.jbmotos.model.entity.Employee;
import com.jbmotos.model.entity.Product;
import com.jbmotos.model.entity.ProductsOfSale;
import com.jbmotos.model.entity.Sale;
import com.jbmotos.model.repositories.SaleRepository;
import com.jbmotos.services.CustomerService;
import com.jbmotos.services.EmployeeService;
import com.jbmotos.services.CardPaymentService;
import com.jbmotos.services.ProductService;
import com.jbmotos.services.ProductsOfSaleService;
import com.jbmotos.services.SaleService;
import com.jbmotos.services.exception.ObjectNotFoundException;

@Service
public class SaleServiceImpl implements SaleService {

	private static final String SALE_NOT_FOUND_MSG = "Venda não encontrada para o Id informado.";

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductsOfSaleService productsOfSaleService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private EmployeeService employeeService;
	
	@Lazy
	@Autowired
	private CardPaymentService cardPaymentService;

	@Autowired
	private ModelMapper mapper;

	@Override
	@Transactional
	public Sale saveSale(SaleDTO saleDTO) {
		Sale sale = this.mapper.map(saleDTO, Sale.class);

		CustomerDTO customerDTO = saleDTO.getCustomer();
		Customer customer = this.customerService.findCustomerByCpf(customerDTO.getCpf());
		sale.setCustomer(customer);

		EmployeeDTO employeeDTO = saleDTO.getEmployee();
		Employee employee = this.employeeService.findEmployeeByCpf(employeeDTO.getCpf());
		sale.setEmployee(employee);
		
		List<ProductsOfSaleDTO> productsOfSaleList = saleDTO.getProductsOfSale();
		
		sale.setProductsOfSale(new ArrayList<>());
		sale.setCardPayment(null);
		Sale saleSaved = this.saleRepository.save(sale);
		
		if (productsOfSaleList != null) {
			BigDecimal totalSaleValue = new BigDecimal(0);

			for (ProductsOfSaleDTO productOfSale : productsOfSaleList) {
				productOfSale.setSaleId(saleSaved.getId());
				ProductsOfSale productsOfSaleSaved = this.productsOfSaleService.saveProductsOfSale(productOfSale);
				totalSaleValue = totalSaleValue.add(productsOfSaleSaved.getTotalValue());
			}

			saleSaved.setTotalSaleValue(totalSaleValue);
		}
		
		if (saleDTO.getPaymentMethod().equals("Cartão de Crédito")) {
			CardPaymentDTO cardPaymentDTO = saleDTO.getCardPayment();
			cardPaymentDTO.setSaleId(saleSaved.getId());
			
			this.cardPaymentService.saveCardPayment(cardPaymentDTO);
		}

		return saleSaved;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Sale> findAllSales() {
		return this.saleRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Sale findSaleById(Integer id) {
		return this.saleRepository.findById(id)
				.orElseThrow(() -> new ObjectNotFoundException(SALE_NOT_FOUND_MSG));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Sale> filterSale(SaleDTO saleDTO) {
		Sale sale = this.mapper.map(saleDTO, Sale.class);

		Example<Sale> example = Example.of(sale,
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

		return this.saleRepository.findAll(example, sort);
	}

	@Override
	@Transactional
	public Sale updateSale(SaleDTO saleDTO) {
		Sale sale = this.mapper.map(saleDTO, Sale.class);

		LocalDateTime dateTime = findSaleById(saleDTO.getId()).getCreatedAt();
		sale.setCreatedAt(dateTime);

		CustomerDTO customerDTO = saleDTO.getCustomer();
		Customer customer = this.customerService.findCustomerByCpf(customerDTO.getCpf());
		sale.setCustomer(customer);

		EmployeeDTO employeeDTO = saleDTO.getEmployee();
		Employee employee = this.employeeService.findEmployeeByCpf(employeeDTO.getCpf());
		sale.setEmployee(employee);

		List<ProductsOfSaleDTO> productsOfSaleList = saleDTO.getProductsOfSale();

		if (productsOfSaleList != null) {
			BigDecimal totalSaleValue = new BigDecimal(0);

			for (ProductsOfSaleDTO productOfSale : productsOfSaleList) {
				totalSaleValue = totalSaleValue.add(productOfSale.getTotalValue());
			}

			sale.setTotalSaleValue(totalSaleValue);
		}

		return this.saleRepository.save(sale);
	}

	@Override
	@Transactional
	public void deleteSaleById(Integer id) {
		this.validateSale(id);

		Sale sale = this.findSaleById(id);

		sale.getProductsOfSale().forEach(product -> {
			this.productsOfSaleService.updateStockQuantityToDelete(product.getId());
		});

		this.saleRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal calculateSaleProfit(Integer saleId) {
		this.validateSale(saleId);
		List<ProductsOfSale> productsOfSaleList = this.productsOfSaleService.findProductsOfSaleBySaleId(saleId);
		return productsOfSaleList.stream()
				.map(productOfSale -> this.productService.calculateProductProfit(productOfSale.getProduct().getId())
						.multiply(BigDecimal.valueOf(productOfSale.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Override
	public void validateSale(Integer id) {
		if (!this.saleRepository.existsById(id)) {
			throw new ObjectNotFoundException(SALE_NOT_FOUND_MSG);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Product> findProductsOfSaleBySaleId(Integer saleId) {
		return this.productsOfSaleService.findProductsOfSaleBySaleId(saleId).stream()
				.map(ProductsOfSale::getProduct).collect(Collectors.toList());
	}
}