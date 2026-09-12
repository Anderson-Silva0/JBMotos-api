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

import com.jbmotos.api.dto.CustomerDTO;
import com.jbmotos.api.dto.MotorcycleDTO;
import com.jbmotos.model.entity.Customer;
import com.jbmotos.model.entity.Motorcycle;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.model.repositories.MotorcycleRepository;
import com.jbmotos.services.CustomerService;
import com.jbmotos.services.exception.BusinessRuleException;
import com.jbmotos.services.exception.ObjectNotFoundException;

@ExtendWith(MockitoExtension.class)
class MotorcycleServiceImplTest {

    @InjectMocks
    private MotorcycleServiceImpl motorcycleService;

    @Mock
    private MotorcycleRepository motorcycleRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private ModelMapper mapper;

    private Motorcycle motorcycle;
    private MotorcycleDTO motorcycleDTO;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.builder().cpf("123.456.789-10").name("Cliente").build();
        motorcycle = Motorcycle.builder().id(1).plate("ABC1234").brand("Honda").model("CG 160").motorcycleStatus(Situation.ACTIVE).createdAt(LocalDateTime.now()).customer(customer).build();
        motorcycleDTO = MotorcycleDTO.builder().id(1).plate("ABC1234").brand("Honda").model("CG 160").customer(CustomerDTO.builder().cpf("123.456.789-10").build()).build();
    }

    @Test
    @DisplayName("Deve salvar moto com sucesso")
    void saveMotorcycle() {
        when(mapper.map(motorcycleDTO, Motorcycle.class)).thenReturn(motorcycle);
        when(customerService.findCustomerByCpf("123.456.789-10")).thenReturn(customer);
        when(motorcycleRepository.save(motorcycle)).thenReturn(motorcycle);

        Motorcycle saved = motorcycleService.saveMotorcycle(motorcycleDTO);

        assertNotNull(saved);
        assertEquals("ABC1234", saved.getPlate());
    }

    @Test
    @DisplayName("Deve lançar erro ao salvar placa duplicada")
    void saveMotorcycleWithDuplicatePlate() {
        when(motorcycleRepository.existsMotorcycleByPlate("ABC1234")).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> motorcycleService.validateMotorcyclePlateToSave("ABC1234"));

        assertEquals("Erro ao tentar salvar, Placa já cadastrada.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve buscar moto por id")
    void findMotorcycleById() {
        when(motorcycleRepository.findById(1)).thenReturn(Optional.of(motorcycle));

        Motorcycle found = motorcycleService.findMotorcycleById(1);

        assertEquals(motorcycle, found);
    }

    @Test
    @DisplayName("Deve buscar moto por placa")
    void findMotorcycleByPlate() {
        when(motorcycleRepository.findMotorcycleByPlate("ABC1234")).thenReturn(Optional.of(motorcycle));

        Motorcycle found = motorcycleService.findMotorcycleByPlate("abc1234");

        assertEquals(motorcycle, found);
    }

    @Test
    @DisplayName("Deve listar motos por CPF do cliente")
    void findMotorcycleByCustomerCpf() {
        doNothing().when(customerService).checkExistingCustomerCpf("123.456.789-10");
        when(motorcycleRepository.existsMotorcycleByCustomerCpf("123.456.789-10")).thenReturn(true);
        when(motorcycleRepository.findMotorcyclesByCustomerCpf("123.456.789-10")).thenReturn(List.of(motorcycle));

        List<Motorcycle> result = motorcycleService.findMotorcycleByCustomerCpf("123.456.789-10");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve lançar erro quando moto não existe")
    void findMotorcycleByIdNotFound() {
        when(motorcycleRepository.findById(99)).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> motorcycleService.findMotorcycleById(99));

        assertEquals("Moto não encontrada para o Id informado.", exception.getMessage());
    }
}
