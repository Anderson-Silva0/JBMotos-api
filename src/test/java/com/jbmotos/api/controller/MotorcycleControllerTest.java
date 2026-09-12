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
import com.jbmotos.api.dto.CustomerDTO;
import com.jbmotos.api.dto.MotorcycleDTO;
import com.jbmotos.model.entity.Customer;
import com.jbmotos.model.entity.Motorcycle;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.services.MotorcycleService;

@ExtendWith(MockitoExtension.class)
class MotorcycleControllerTest {

    @InjectMocks
    private MotorcycleController controller;

    @Mock
    private MotorcycleService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "mapper", new ModelMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(null).build();
    }

    @Test
    @DisplayName("Deve salvar moto")
    void saveMotorcycle() throws Exception {
        MotorcycleDTO dto = MotorcycleDTO.builder().plate("ABC12345").brand("Honda").model("CG 160").year(2023).customer(CustomerDTO.builder().cpf("769.874.236-95").name("Cliente A").email("cliente@email.com").phone("(81) 98888-8888").build()).build();
        Motorcycle motorcycle = Motorcycle.builder().id(1).plate(dto.getPlate()).brand(dto.getBrand()).model(dto.getModel()).year(dto.getYear()).motorcycleStatus(Situation.ACTIVE).customer(Customer.builder().cpf(dto.getCustomer().getCpf()).name(dto.getCustomer().getName()).email(dto.getCustomer().getEmail()).phone(dto.getCustomer().getPhone()).build()).build();

        when(service.saveMotorcycle(any(MotorcycleDTO.class))).thenReturn(motorcycle);

        mockMvc.perform(post("/api/motorcycle").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.plate").value("ABC12345"));
    }

    @Test
    @DisplayName("Deve listar motos")
    void findAll() throws Exception {
        when(service.findAllMotorcycles()).thenReturn(List.of(Motorcycle.builder().id(1).plate("ABC1234").brand("Honda").model("CG 160").build()));

        mockMvc.perform(get("/api/motorcycle/find-all")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar moto por placa")
    void findByPlate() throws Exception {
        Motorcycle motorcycle = Motorcycle.builder().id(1).plate("ABC1234").brand("Honda").model("CG 160").build();
        when(service.findMotorcycleByPlate("ABC1234")).thenReturn(motorcycle);

        mockMvc.perform(get("/api/motorcycle/find-by-plate/ABC1234")).andExpect(status().isOk()).andExpect(jsonPath("$.plate").value("ABC1234"));
    }

    @Test
    @DisplayName("Deve deletar moto")
    void deleteMotorcycle() throws Exception {
        doNothing().when(service).deleteMotorcycleById(1);

        mockMvc.perform(delete("/api/motorcycle/delete-by-id/1")).andExpect(status().isNoContent());
    }
}
