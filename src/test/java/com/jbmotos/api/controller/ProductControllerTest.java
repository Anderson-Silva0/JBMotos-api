package com.jbmotos.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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
import com.jbmotos.model.entity.Product;
import com.jbmotos.model.entity.Stock;
import com.jbmotos.model.entity.Supplier;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.model.enums.StockStatus;
import com.jbmotos.services.ProductService;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(productController, "mapper", new ModelMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @DisplayName("Deve salvar produto via controller")
    void saveProduct() throws Exception {
        ProductDTO dto = ProductDTO.builder()
                .name("Pneu")
                .costPrice(BigDecimal.valueOf(100.00))
                .salePrice(BigDecimal.valueOf(150.00))
                .brand("Vipal")
                .stockId(1)
                .supplierCnpj("21.300.144/0001-33")
                .build();

        Product product = Product.builder()
                .id(1)
                .name(dto.getName())
                .costPrice(dto.getCostPrice())
                .salePrice(dto.getSalePrice())
                .brand(dto.getBrand())
                .productStatus(Situation.ACTIVE)
                .stock(Stock.builder().id(1).quantity(10).status(StockStatus.AVAILABLE).build())
                .supplier(Supplier.builder().cnpj(dto.getSupplierCnpj()).name("Maringá").build())
                .build();

        when(productService.saveProduct(any(ProductDTO.class))).thenReturn(product);

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pneu"));
    }

    @Test
    @DisplayName("Deve listar produtos via controller")
    void findAllProducts() throws Exception {
        Product product = buildProduct();
        when(productService.findAllProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/product/find-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pneu"));
    }

    @Test
    @DisplayName("Deve buscar produto por id via controller")
    void findProductById() throws Exception {
        Product product = buildProduct();
        when(productService.findProductById(1)).thenReturn(product);

        mockMvc.perform(get("/api/product/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Deve deletar produto via controller")
    void deleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(1);

        mockMvc.perform(delete("/api/product/delete/1"))
                .andExpect(status().isNoContent());
    }

    private Product buildProduct() {
        return Product.builder()
                .id(1)
                .name("Pneu")
                .costPrice(BigDecimal.valueOf(100.00))
                .salePrice(BigDecimal.valueOf(150.00))
                .brand("Vipal")
                .productStatus(Situation.ACTIVE)
                .stock(Stock.builder().id(1).quantity(10).status(StockStatus.AVAILABLE).build())
                .supplier(Supplier.builder().cnpj("21.300.144/0001-33").name("Maringá").build())
                .build();
    }
}
