package com.jbmotos.api.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jbmotos.api.dto.ProductDTO;
import com.jbmotos.model.entity.Product;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.services.ProductService;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<ProductDTO> save(@Valid @RequestBody ProductDTO productDTO) {
        Product product = this.productService.saveProduct(productDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(product).toUri();
        return ResponseEntity.created(uri).body(this.mapper.map(product, ProductDTO.class));
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<ProductDTO>> findAll() {
        return ResponseEntity.ok().body(
                this.productService.findAllProducts().stream().map(produto ->
                        this.mapper.map(produto, ProductDTO.class)
                        ).collect(Collectors.toList()));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(this.mapper.map(this.productService.findProductById(id), ProductDTO.class));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProductDTO>> filter(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "productStatus", required = false) String productStatus
    ) {
        ProductDTO productDTO = ProductDTO.builder()
                .name(name)
                .brand(brand)
                .productStatus(productStatus)
                .build();
        return ResponseEntity.ok().body(
                this.productService.filterProduct(productDTO).stream().map(product ->
                        this.mapper.map(product, ProductDTO.class)
                ).collect(Collectors.toList()));
    }

    @PatchMapping("/toggle-status/{id}")
    public ResponseEntity<Situation> toggleStatus(@PathVariable("id") Integer id) {
    	Situation productStatus = this.productService.toggleProductStatus(id);
        return ResponseEntity.ok().body(productStatus);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable("id") Integer id,
                                             @Valid @RequestBody ProductDTO productDTO) {
        productDTO.setId(id);
        return ResponseEntity.ok().body(this.mapper.map(this.productService.updateProduct(productDTO), ProductDTO.class));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        this.productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/product-profit/{productId}")
    public ResponseEntity<BigDecimal> productProfit(@PathVariable("productId") Integer productId) {
        return ResponseEntity.ok().body(this.productService.calculateProductProfit(productId));
    }
}
