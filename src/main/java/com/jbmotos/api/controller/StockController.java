package com.jbmotos.api.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.jbmotos.model.entity.Stock;
import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import com.jbmotos.api.dto.StockDTO;
import com.jbmotos.services.StockService;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<StockDTO> save(@Valid @RequestBody StockDTO stockDTO){
        Stock stock = this.stockService.saveStock(stockDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(stock).toUri();
        return ResponseEntity.created(uri).body(this.mapper.map(stock, StockDTO.class));
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<StockDTO>> findAll(){
        return ResponseEntity.ok().body(
                this.stockService.findAllStocks().stream().map(estoque ->
                        this.mapper.map(estoque, StockDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<StockDTO> findById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(this.mapper.map(this.stockService.findStockById(id), StockDTO.class));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<StockDTO> update(@PathVariable("id") Integer id,
                                           @Valid @RequestBody StockDTO stockDTO) {
        stockDTO.setId(id);
        return ResponseEntity.ok().body(this.mapper.map(this.stockService.updateStock(stockDTO), StockDTO.class));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        this.stockService.deleteStockById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/add")
    public ResponseEntity<String> addQuantity(@PathVariable("productId") Integer productId,
                                              @RequestParam Integer productQuantity) {
        this.stockService.addStockQuantity(productId, productQuantity);
        Integer productQuantityInStock = this.stockService.getProductQuantity(productId);
        return ResponseEntity.ok().body("A quantidade de " + productQuantity +
                " foi adicionada ao estoque. O estoque atual " +
                "é de " + productQuantityInStock);
    }

    @GetMapping("/total-stock-cost-value")
    public ResponseEntity<BigDecimal> totalValueStockCost() {
        return ResponseEntity.ok().body(this.stockService.calculateTotalStockCost());
    }

    @GetMapping("/sales-potential-stock")
    public ResponseEntity<BigDecimal> potentialStockSale() {
        return ResponseEntity.ok().body(this.stockService.calculateStockSalesPotential());
    }
}
