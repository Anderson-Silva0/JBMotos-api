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
import com.jbmotos.api.dto.EmployeeDTO;
import com.jbmotos.model.entity.Address;
import com.jbmotos.model.entity.Employee;
import com.jbmotos.services.EmployeeService;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController controller;

    @Mock
    private EmployeeService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "mapper", new ModelMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Deve salvar funcionário")
    void saveEmployee() throws Exception {
        EmployeeDTO dto = EmployeeDTO.builder().cpf("123.456.789-10").name("Func").phone("(81) 99999-9999").address(AddressDTO.builder().id(1).road("Rua A").cep("50720-000").number(10).neighborhood("Boa Vista").city("Recife").build()).build();
        Employee employee = Employee.builder().cpf(dto.getCpf()).name(dto.getName()).phone(dto.getPhone()).createdAt(LocalDateTime.now()).address(Address.builder().id(1).road("Rua A").build()).build();

        when(service.saveEmployee(any(EmployeeDTO.class))).thenReturn(employee);

        mockMvc.perform(post("/api/employee").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value("123.456.789-10"));
    }

    @Test
    @DisplayName("Deve listar funcionários")
    void findAll() throws Exception {
        when(service.findAllEmployees()).thenReturn(List.of(Employee.builder().cpf("123.456.789-10").name("Func").build()));

        mockMvc.perform(get("/api/employee/find-all")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar funcionário por cpf")
    void findByCpf() throws Exception {
        Employee employee = Employee.builder().cpf("123.456.789-10").name("Func").phone("(81) 99999-9999").build();
        when(service.findEmployeeByCpf("123.456.789-10")).thenReturn(employee);

        mockMvc.perform(get("/api/employee/find/123.456.789-10")).andExpect(status().isOk()).andExpect(jsonPath("$.cpf").value("123.456.789-10"));
    }

    @Test
    @DisplayName("Deve deletar funcionário")
    void deleteEmployee() throws Exception {
        doNothing().when(service).deleteEmployee("123.456.789-10");

        mockMvc.perform(delete("/api/employee/delete/123.456.789-10")).andExpect(status().isNoContent());
    }
}
