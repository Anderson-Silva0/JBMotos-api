package com.jbmotos.api.controller;

import com.jbmotos.api.dto.CustomerDTO;
import com.jbmotos.api.dto.EmployeeDTO;
import com.jbmotos.api.dto.ProductDTO;
import com.jbmotos.api.dto.SaleDTO;
import com.jbmotos.model.entity.Sale;
import com.jbmotos.services.SaleService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sale")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<SaleDTO> save(@Valid @RequestBody SaleDTO saleDTO) {
        Sale sale = this.saleService.saveSale(saleDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(sale).toUri();
        return ResponseEntity.created(uri).body(this.mapper.map(sale, SaleDTO.class));
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<SaleDTO>> findAll() {
        return ResponseEntity.ok().body(
                this.saleService.findAllSales().stream().map(venda ->
                        this.mapper.map(venda, SaleDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<SaleDTO> findById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(this.mapper.map(this.saleService.findSaleById(id), SaleDTO.class));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<SaleDTO>> filter(
            @RequestParam(value = "customerCpf", required = false) String customerCpf,
            @RequestParam(value = "employeeCpf", required = false) String employeeCpf
    ) {
        CustomerDTO customerDTO = CustomerDTO.builder().cpf(customerCpf).build();
        EmployeeDTO employeeDTO = EmployeeDTO.builder().cpf(employeeCpf).build();

        SaleDTO saleDTO = SaleDTO.builder()
                .customer(customerDTO)
                .employee(employeeDTO)
                .build();
        return ResponseEntity.ok().body(
                this.saleService.filterSale(saleDTO).stream().map(sale ->
                        this.mapper.map(sale, SaleDTO.class)
                ).collect(Collectors.toList()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SaleDTO> update(@PathVariable("id") Integer id,
                                          @Valid @RequestBody SaleDTO saleDTO) {
        saleDTO.setId(id);
        return ResponseEntity.ok().body(this.mapper.map(this.saleService.updateSale(saleDTO), SaleDTO.class));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        this.saleService.deleteSaleById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sale-profit/{saleId}")
    public ResponseEntity<BigDecimal> saleProfit(@PathVariable("saleId") Integer saleId) {
        return ResponseEntity.ok().body(this.saleService.calculateSaleProfit(saleId));
    }

    @GetMapping("/find-sale-products/{saleId}")
    public ResponseEntity<List<ProductDTO>> findSaleProducts(@PathVariable("saleId") Integer saleId) {
        return ResponseEntity.ok().body(
                this.saleService.findProductsOfSaleBySaleId(saleId).stream().map(product ->
                        this.mapper.map(product, ProductDTO.class)
                ).collect(Collectors.toList()));
    }
}
