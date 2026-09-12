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
import com.jbmotos.api.dto.CustomerDTO;
import com.jbmotos.api.dto.EmployeeDTO;
import com.jbmotos.api.dto.SaleDTO;
import com.jbmotos.model.entity.Customer;
import com.jbmotos.model.entity.Employee;
import com.jbmotos.model.entity.Sale;
import com.jbmotos.services.SaleService;

@ExtendWith(MockitoExtension.class)
class SaleControllerTest {

    @InjectMocks
    private SaleController controller;

    @Mock
    private SaleService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "mapper", new ModelMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(null).build();
    }

    @Test
    @DisplayName("Deve salvar venda")
    void save() throws Exception {
        SaleDTO dto = SaleDTO.builder().customer(CustomerDTO.builder().cpf("769.874.236-95").name("Cliente A").email("cliente@email.com").phone("(81) 98888-8888").build()).employee(EmployeeDTO.builder().cpf("414.290.891-05").name("Funcionario A").phone("(81) 97777-7777").build()).paymentMethod("Dinheiro").totalSaleValue(new BigDecimal("150.00")).build();
        Sale sale = Sale.builder().id(1).customer(Customer.builder().cpf("769.874.236-95").name("Cliente A").email("cliente@email.com").phone("(81) 98888-8888").build()).employee(Employee.builder().cpf("414.290.891-05").name("Funcionario A").phone("(81) 97777-7777").build()).paymentMethod("Dinheiro").totalSaleValue(new BigDecimal("150.00")).build();

        when(service.saveSale(any(SaleDTO.class))).thenReturn(sale);

        mockMvc.perform(post("/api/sale").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentMethod").value("Dinheiro"));
    }

    @Test
    @DisplayName("Deve listar vendas")
    void findAll() throws Exception {
        when(service.findAllSales()).thenReturn(List.of(Sale.builder().id(1).paymentMethod("Dinheiro").totalSaleValue(new BigDecimal("150.00")).build()));

        mockMvc.perform(get("/api/sale/find-all")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar venda por id")
    void findById() throws Exception {
        Sale sale = Sale.builder().id(1).paymentMethod("Dinheiro").totalSaleValue(new BigDecimal("150.00")).build();
        when(service.findSaleById(1)).thenReturn(sale);

        mockMvc.perform(get("/api/sale/find/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Deve deletar venda")
    void deleteSale() throws Exception {
        doNothing().when(service).deleteSaleById(1);

        mockMvc.perform(delete("/api/sale/delete/1")).andExpect(status().isNoContent());
    }
}
