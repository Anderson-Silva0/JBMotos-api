package com.jbmotos.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jbmotos.api.dto.EmployeeDTO;
import com.jbmotos.api.dto.MotorcycleDTO;
import com.jbmotos.api.dto.RepairDTO;
import com.jbmotos.api.dto.SaleDTO;
import com.jbmotos.model.entity.Employee;
import com.jbmotos.model.entity.Motorcycle;
import com.jbmotos.model.entity.Repair;
import com.jbmotos.model.entity.Sale;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.model.repositories.RepairRepository;
import com.jbmotos.services.EmployeeService;
import com.jbmotos.services.MotorcycleService;
import com.jbmotos.services.SaleService;
import com.jbmotos.services.exception.BusinessRuleException;
import com.jbmotos.services.exception.ObjectNotFoundException;

@ExtendWith(MockitoExtension.class)
class RepairServiceImplTest {

    @InjectMocks
    private RepairServiceImpl repairService;

    @Mock
    private RepairRepository repairRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private MotorcycleService motorcycleService;

    @Mock
    private SaleService saleService;

    @Mock
    private ModelMapper mapper;

    private Repair repair;
    private RepairDTO repairDTO;
    private Employee employee;
    private Motorcycle motorcycle;
    private Sale sale;

    @BeforeEach
    void setUp() {
        employee = Employee.builder().cpf("123.456.789-10").name("Func").employeeStatus(Situation.ACTIVE).build();
        motorcycle = Motorcycle.builder().id(5).plate("ABC1234").brand("Honda").model("CG 160").build();
        sale = Sale.builder().id(1).build();
        repair = Repair.builder().id(1).employee(employee).motorcycle(motorcycle).sale(sale).repairsPerformed("Troca de óleo").createdAt(LocalDateTime.now()).build();
        repairDTO = RepairDTO.builder().id(1).employee(EmployeeDTO.builder().cpf("123.456.789-10").build()).motorcycle(MotorcycleDTO.builder().id(5).build()).sale(SaleDTO.builder().id(1).build()).repairsPerformed("Troca de óleo").build();
    }

    @Test
    @DisplayName("Deve salvar serviço com sucesso")
    void saveRepair() {
        when(saleService.saveSale(repairDTO.getSale())).thenReturn(sale);
        when(employeeService.findEmployeeByCpf("123.456.789-10")).thenReturn(employee);
        when(motorcycleService.findMotorcycleById(5)).thenReturn(motorcycle);
        when(mapper.map(repairDTO, Repair.class)).thenReturn(repair);
        when(repairRepository.save(repair)).thenReturn(repair);

        Repair saved = repairService.saveRepair(repairDTO);

        assertNotNull(saved);
        assertEquals("Troca de óleo", saved.getRepairsPerformed());
    }

    @Test
    @DisplayName("Deve buscar serviço por id")
    void findRepairById() {
        when(repairRepository.findById(1)).thenReturn(Optional.of(repair));

        Repair result = repairService.findRepairById(1);

        assertEquals(repair, result);
    }

    @Test
    @DisplayName("Deve localizar serviço por venda")
    void findRepairBySaleId() {
        doNothing().when(saleService).validateSale(1);
        when(repairRepository.findRepairBySaleId(1)).thenReturn(Optional.of(repair));

        Repair result = repairService.findRepairBySaleId(1);

        assertEquals(repair, result);
    }

    @Test
    @DisplayName("Deve listar serviços por cpf do funcionário")
    void findRepairByEmployeeCpf() {
        doNothing().when(employeeService).checkExistingEmployeeCpf("123.456.789-10");
        when(repairRepository.findRepairByEmployeeCpf("123.456.789-10")).thenReturn(List.of(repair));

        List<Repair> result = repairService.findRepairByEmployeeCpf("123.456.789-10");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve falhar ao buscar serviço inexistente")
    void findRepairByIdNotFound() {
        when(repairRepository.findById(99)).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> repairService.findRepairById(99));

        assertEquals("Serviço não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao buscar serviço por venda sem relacionamento")
    void findRepairBySaleIdWithoutRepair() {
        doNothing().when(saleService).validateSale(88);
        when(repairRepository.findRepairBySaleId(88)).thenReturn(Optional.empty());

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> repairService.findRepairBySaleId(88));

        assertEquals("A Venda informada não pertence a um Serviço.", exception.getMessage());
    }
}
