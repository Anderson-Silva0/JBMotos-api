package com.jbmotos.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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
import com.jbmotos.api.dto.AddressDTO;
import com.jbmotos.api.dto.CustomerDTO;
import com.jbmotos.model.entity.Address;
import com.jbmotos.model.entity.Customer;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.services.CustomerService;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @InjectMocks
    private CustomerController customerController;

    @Mock
    private CustomerService customerService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(customerController, "mapper", new ModelMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    @DisplayName("Deve salvar cliente via controller")
    void saveCustomer() throws Exception {
        CustomerDTO dto = CustomerDTO.builder()
                .cpf("123.456.789-10")
                .name("João Silva")
                .email("joao@email.com")
                .phone("(81) 98888-8888")
                .address(AddressDTO.builder()
                        .id(1)
                        .road("Rua A")
                        .cep("50720-000")
                        .number(123)
                        .neighborhood("Boa Vista")
                        .city("Recife")
                        .build())
                .build();

        Customer customer = Customer.builder()
                .cpf(dto.getCpf())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .customerStatus(Situation.ACTIVE)
                .address(Address.builder().id(1).road("Rua A").cep("50720-000").number(123).neighborhood("Boa Vista").city("Recife").build())
                .createdAt(LocalDateTime.now())
                .build();

        when(customerService.saveCustomer(any(CustomerDTO.class))).thenReturn(customer);

        mockMvc.perform(post("/api/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value("123.456.789-10"));
    }

    @Test
    @DisplayName("Deve buscar cliente por cpf via controller")
    void findByCpf() throws Exception {
        Customer customer = buildCustomer();
        when(customerService.findCustomerByCpf("123.456.789-10")).thenReturn(customer);

        mockMvc.perform(get("/api/customer/find/123.456.789-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("123.456.789-10"));
    }

    @Test
    @DisplayName("Deve listar clientes via controller")
    void findAll() throws Exception {
        when(customerService.findAllCustomer()).thenReturn(List.of(buildCustomer()));

        mockMvc.perform(get("/api/customer/find-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cpf").value("123.456.789-10"));
    }

    @Test
    @DisplayName("Deve deletar cliente via controller")
    void deleteCustomer() throws Exception {
        doNothing().when(customerService).deleteCustomer("123.456.789-10");

        mockMvc.perform(delete("/api/customer/delete/123.456.789-10"))
                .andExpect(status().isNoContent());
    }

    private Customer buildCustomer() {
        return Customer.builder()
                .cpf("123.456.789-10")
                .name("João Silva")
                .email("joao@email.com")
                .phone("(81) 98888-8888")
                .customerStatus(Situation.ACTIVE)
                .address(Address.builder().id(1).road("Rua A").build())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
