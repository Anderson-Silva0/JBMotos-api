package com.jbmotos.services.impl;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jbmotos.api.dto.CustomerDTO;
import com.jbmotos.api.dto.EmployeeDTO;
import com.jbmotos.api.dto.SaleDTO;
import com.jbmotos.model.entity.Sale;

class SaleServiceImplTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void saveSale() {
    }

    @Test
    void findAllSales() {
    }

    @Test
    void findSaleById() {
    }

    @Test
    void updateSale() {
    }

    @Test
    void deleteSaleById() {
    }

    @Test
    void calculateSaleProfit() {
    }

    @Test
    void validateSale() {
    }

    @Test
    void valorTotalDaVenda() {
    }

    @Test
    void findProductsOfSaleBySaleId() {
    }

    public static Sale getVenda() {
        return Sale.builder()
                .id(1)
                .customer(null)
                .employee(null)
                .createdAt(LocalDateTime.now())
                .observation("")
                .paymentMethod("PIX")
                .build();
    }

    public static SaleDTO getVendaDTO() {
        return SaleDTO.builder()
                .id(1)
                .customer(CustomerDTO.builder().cpf("123.456.789-10").build())
                .employee(EmployeeDTO.builder().cpf("109.876.543-21").build())
                .createdAt(LocalDateTime.now())
                .observation("")
                .paymentMethod("PIX")
                .build();
    }
}