package com.jbmotos.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.jbmotos.model.entity.Address;
import com.jbmotos.services.AddressService;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @InjectMocks
    private AddressController controller;

    @Mock
    private AddressService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "mapper", new ModelMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Deve salvar endereço")
    void saveAddress() throws Exception {
        AddressDTO dto = AddressDTO.builder().road("Rua A").cep("50720-000").number(123).neighborhood("Boa Vista").city("Recife").build();
        Address address = Address.builder().id(1).road(dto.getRoad()).cep(dto.getCep()).number(dto.getNumber()).neighborhood(dto.getNeighborhood()).city(dto.getCity()).build();

        when(service.saveAddress(any(AddressDTO.class))).thenReturn(address);

        mockMvc.perform(post("/api/address").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.road").value("Rua A"));
    }

    @Test
    @DisplayName("Deve listar endereços")
    void findAll() throws Exception {
        when(service.findAllAddress()).thenReturn(java.util.List.of(Address.builder().id(1).road("Rua A").cep("50720-000").number(123).neighborhood("Boa Vista").city("Recife").build()));

        mockMvc.perform(get("/api/address/find-all")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar endereço por id")
    void findById() throws Exception {
        Address address = Address.builder().id(1).road("Rua A").cep("50720-000").number(123).neighborhood("Boa Vista").city("Recife").build();
        when(service.findAddressById(1)).thenReturn(address);

        mockMvc.perform(get("/api/address/find/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Deve deletar endereço")
    void deleteAddress() throws Exception {
        doNothing().when(service).deleteAddressById(1);

        mockMvc.perform(delete("/api/address/delete/1")).andExpect(status().isNoContent());
    }
}
