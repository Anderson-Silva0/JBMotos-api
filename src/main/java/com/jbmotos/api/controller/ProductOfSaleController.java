package com.jbmotos.api.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.jbmotos.api.dto.ProductDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jbmotos.api.dto.ProductsOfSaleDTO;
import com.jbmotos.model.entity.ProductsOfSale;
import com.jbmotos.services.ProductsOfSaleService;

@RestController
@RequestMapping("/api/product-of-sale")
@Validated
public class ProductOfSaleController {

    @Autowired
    private ProductsOfSaleService productsOfSaleService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<ProductsOfSaleDTO> save(@Valid @RequestBody ProductsOfSaleDTO productsOfSaleDTO){
        ProductsOfSale productsOfSale = this.productsOfSaleService.saveProductsOfSale(productsOfSaleDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(productsOfSale).toUri();
        return ResponseEntity.created(uri).body(this.mapper.map(productsOfSale, ProductsOfSaleDTO.class));
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<ProductsOfSaleDTO>> findAll(){
        return ResponseEntity.ok().body(
                this.productsOfSaleService.findAllProductsOfSale().stream().map(productOfSale ->
                        this.mapper.map(productOfSale, ProductsOfSaleDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<ProductsOfSaleDTO> findById(@PathVariable("id") Integer id){
        return ResponseEntity.ok().body(this.mapper.map(this.productsOfSaleService.
                findProductsOfSaleById(id), ProductsOfSaleDTO.class));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductsOfSaleDTO> update(@PathVariable("id") Integer id,
                                                    @RequestParam Integer saleId,
                                                    Integer productId,
                                                    @Positive(message = "A quantidade deve ser maior que zero") Integer quantity) {

        ProductDTO productDTO = ProductDTO.builder().id(productId).build();

        ProductsOfSaleDTO productsOfSaleDTO = ProductsOfSaleDTO.builder()
                .id(id)
                .saleId(saleId)
                .product(productDTO)
                .quantity(quantity)
                .build();
        return ResponseEntity.ok().body(
                this.mapper.map(this.productsOfSaleService.updateProductsOfSale(productsOfSaleDTO), ProductsOfSaleDTO.class)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id){
        this.productsOfSaleService.deleteProductsOfSaleById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products-of-sale/{saleId}")
    public ResponseEntity<List<ProductsOfSaleDTO>> findAllBySaleId(@PathVariable("saleId") Integer saleId){
        return ResponseEntity.ok().body(
                this.productsOfSaleService.findProductsOfSaleBySaleId(saleId).stream().map(productOfSale ->
                        this.mapper.map(productOfSale, ProductsOfSaleDTO.class)
                ).collect(Collectors.toList()));
    }
}
