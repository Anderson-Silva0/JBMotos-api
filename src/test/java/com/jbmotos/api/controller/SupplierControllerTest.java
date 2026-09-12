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
import com.jbmotos.api.dto.AddressDTO;
import com.jbmotos.api.dto.SupplierDTO;
import com.jbmotos.model.entity.Address;
import com.jbmotos.model.entity.Supplier;
import com.jbmotos.services.SupplierService;

@ExtendWith(MockitoExtension.class)
class SupplierControllerTest {

    @InjectMocks
    private SupplierController controller;

    @Mock
    private SupplierService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "mapper", new ModelMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(null).build();
    }

    @Test
    @DisplayName("Deve salvar fornecedor")
    void saveSupplier() throws Exception {
        SupplierDTO dto = SupplierDTO.builder().cnpj("76.863.307/5193-37").name("Fornecedor A").phone("(81) 99999-9999").address(AddressDTO.builder().road("Rua A").cep("50720-000").number(10).neighborhood("Boa Vista").city("Recife").build()).build();
        Supplier supplier = Supplier.builder().cnpj(dto.getCnpj()).name(dto.getName()).phone(dto.getPhone()).address(Address.builder().road("Rua A").cep("50720-000").number(10).neighborhood("Boa Vista").city("Recife").build()).build();

        when(service.saveSupplier(any(SupplierDTO.class))).thenReturn(supplier);

        mockMvc.perform(post("/api/supplier").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cnpj").value("76.863.307/5193-37"));
    }

    @Test
    @DisplayName("Deve listar fornecedores")
    void findAll() throws Exception {
        when(service.findAllSuppliers()).thenReturn(List.of(Supplier.builder().cnpj("11.111.111/0001-11").name("Fornecedor A").phone("(81) 99999-9999").build()));

        mockMvc.perform(get("/api/supplier/find-all")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar fornecedor por cnpj")
    void findByCnpj() throws Exception {
        Supplier supplier = Supplier.builder().cnpj("11.111.111/0001-11").name("Fornecedor A").phone("(81) 99999-9999").build();
        when(service.findSupplierByCnpj("11.111.111/0001-11")).thenReturn(supplier);

        mockMvc.perform(get("/api/supplier/find").param("cnpj", "11.111.111/0001-11")).andExpect(status().isOk()).andExpect(jsonPath("$.cnpj").value("11.111.111/0001-11"));
    }

    @Test
    @DisplayName("Deve deletar fornecedor")
    void deleteSupplier() throws Exception {
        doNothing().when(service).deleteSupplier("11.111.111/0001-11");

        mockMvc.perform(delete("/api/supplier/delete").param("cnpj", "11.111.111/0001-11")).andExpect(status().isNoContent());
    }
}
