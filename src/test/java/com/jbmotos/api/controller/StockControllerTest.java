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
import com.jbmotos.api.dto.StockDTO;
import com.jbmotos.model.entity.Stock;
import com.jbmotos.model.enums.StockStatus;
import com.jbmotos.services.StockService;

@ExtendWith(MockitoExtension.class)
class StockControllerTest {

    @InjectMocks
    private StockController controller;

    @Mock
    private StockService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "mapper", new ModelMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Deve salvar estoque")
    void save() throws Exception {
        StockDTO dto = StockDTO.builder().minStock(10).maxStock(100).quantity(25).status("AVAILABLE").build();
        Stock stock = Stock.builder().id(1).minStock(dto.getMinStock()).maxStock(dto.getMaxStock()).quantity(dto.getQuantity()).status(StockStatus.valueOf(dto.getStatus())).build();

        when(service.saveStock(any(StockDTO.class))).thenReturn(stock);

        mockMvc.perform(post("/api/stock").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(25));
    }

    @Test
    @DisplayName("Deve listar estoques")
    void findAll() throws Exception {
        when(service.findAllStocks()).thenReturn(List.of(Stock.builder().id(1).minStock(10).maxStock(100).quantity(25).status(StockStatus.AVAILABLE).build()));

        mockMvc.perform(get("/api/stock/find-all")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar estoque por id")
    void findById() throws Exception {
        Stock stock = Stock.builder().id(1).minStock(10).maxStock(100).quantity(25).status(StockStatus.AVAILABLE).build();
        when(service.findStockById(1)).thenReturn(stock);

        mockMvc.perform(get("/api/stock/find/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Deve calcular valor total do estoque")
    void totalValueStockCost() throws Exception {
        when(service.calculateTotalStockCost()).thenReturn(new BigDecimal("250.00"));

        mockMvc.perform(get("/api/stock/total-stock-cost-value")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve deletar estoque")
    void deleteStock() throws Exception {
        doNothing().when(service).deleteStockById(1);

        mockMvc.perform(delete("/api/stock/delete/1")).andExpect(status().isNoContent());
    }
}
