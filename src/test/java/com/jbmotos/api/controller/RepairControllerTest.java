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
import com.jbmotos.api.dto.EmployeeDTO;
import com.jbmotos.api.dto.MotorcycleDTO;
import com.jbmotos.api.dto.RepairDTO;
import com.jbmotos.model.entity.Customer;
import com.jbmotos.model.entity.Employee;
import com.jbmotos.model.entity.Motorcycle;
import com.jbmotos.model.entity.Repair;
import com.jbmotos.services.RepairService;

@ExtendWith(MockitoExtension.class)
class RepairControllerTest {

    @InjectMocks
    private RepairController controller;

    @Mock
    private RepairService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "mapper", new ModelMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(null).build();
    }

    @Test
    @DisplayName("Deve salvar reparo")
    void save() throws Exception {
        RepairDTO dto = RepairDTO.builder().repairsPerformed("Troca de óleo").employee(EmployeeDTO.builder().cpf("414.290.891-05").name("Funcionario A").phone("(81) 97777-7777").build()).motorcycle(MotorcycleDTO.builder().id(5).year(2023).customer(CustomerDTO.builder().cpf("769.874.236-95").name("Cliente A").email("cliente@email.com").phone("(81) 98888-8888").build()).plate("ABC12345").brand("Honda").model("CG 160").build()).build();
        Repair repair = Repair.builder().id(1).repairsPerformed(dto.getRepairsPerformed()).employee(Employee.builder().cpf(dto.getEmployee().getCpf()).name(dto.getEmployee().getName()).phone(dto.getEmployee().getPhone()).build()).motorcycle(Motorcycle.builder().id(5).plate("ABC12345").brand("Honda").model("CG 160").year(2023).customer(Customer.builder().cpf("769.874.236-95").name("Cliente A").email("cliente@email.com").phone("(81) 98888-8888").build()).build()).build();

        when(service.saveRepair(any(RepairDTO.class))).thenReturn(repair);

        mockMvc.perform(post("/api/repair").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.repairsPerformed").value("Troca de óleo"));
    }

    @Test
    @DisplayName("Deve listar reparos")
    void findAll() throws Exception {
        when(service.findAllRepairs()).thenReturn(List.of(Repair.builder().id(1).repairsPerformed("Troca de óleo").build()));

        mockMvc.perform(get("/api/repair/find-all")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar reparo por id")
    void findById() throws Exception {
        Repair repair = Repair.builder().id(1).repairsPerformed("Troca de óleo").build();
        when(service.findRepairById(1)).thenReturn(repair);

        mockMvc.perform(get("/api/repair/find/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Deve deletar reparo")
    void deleteRepair() throws Exception {
        doNothing().when(service).deleteRepair(1);

        mockMvc.perform(delete("/api/repair/delete/1")).andExpect(status().isNoContent());
    }
}
