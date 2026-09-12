package com.jbmotos.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbmotos.api.dto.ProductDTO;
import com.jbmotos.api.dto.ProductsOfSaleDTO;
import com.jbmotos.model.entity.Product;
import com.jbmotos.model.entity.ProductsOfSale;
import com.jbmotos.services.ProductsOfSaleService;

@ExtendWith(MockitoExtension.class)
class ProductOfSaleControllerTest {

    @InjectMocks
    private ProductOfSaleController controller;

    @Mock
    private ProductsOfSaleService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "mapper", new ModelMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Deve salvar item de venda")
    void save() throws Exception {
        ProductsOfSaleDTO dto = ProductsOfSaleDTO.builder().saleId(1).product(ProductDTO.builder().id(3).name("Produto A").build()).quantity(2).build();
        ProductsOfSale item = ProductsOfSale.builder().id(1).sale(com.jbmotos.model.entity.Sale.builder().id(1).build()).product(Product.builder().id(3).name("Produto A").build()).quantity(2).build();

        when(service.saveProductsOfSale(any(ProductsOfSaleDTO.class))).thenReturn(item);

        mockMvc.perform(post("/api/product-of-sale").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    @DisplayName("Deve listar itens da venda")
    void findAll() throws Exception {
        when(service.findAllProductsOfSale()).thenReturn(List.of(ProductsOfSale.builder().id(1).sale(com.jbmotos.model.entity.Sale.builder().id(1).build()).quantity(2).build()));

        mockMvc.perform(get("/api/product-of-sale/find-all")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar item por id")
    void findById() throws Exception {
        ProductsOfSale item = ProductsOfSale.builder().id(1).sale(com.jbmotos.model.entity.Sale.builder().id(1).build()).quantity(2).build();
        when(service.findProductsOfSaleById(1)).thenReturn(item);

        mockMvc.perform(get("/api/product-of-sale/find/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Deve deletar item de venda")
    void deleteProductOfSale() throws Exception {
        doNothing().when(service).deleteProductsOfSaleById(1);

        mockMvc.perform(delete("/api/product-of-sale/delete/1")).andExpect(status().isNoContent());
    }
}
