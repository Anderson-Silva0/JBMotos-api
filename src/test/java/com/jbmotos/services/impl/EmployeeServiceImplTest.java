package com.jbmotos.services.impl;

import com.jbmotos.api.dto.EmployeeDTO;
import com.jbmotos.model.entity.Address;
import com.jbmotos.model.entity.Employee;
import com.jbmotos.model.repositories.EmployeeRepository;
import com.jbmotos.services.AddressService;
import com.jbmotos.services.exception.ObjectNotFoundException;
import com.jbmotos.services.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class EmployeeServiceImplTest {

    @Autowired
    private EmployeeServiceImpl funcionarioService;

    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private AddressService addressService;

    @MockBean
    private ModelMapper mapper;

    private Employee employee;
    private EmployeeDTO employeeDTO;
    private Address address;


    @BeforeEach
    void setUp() {
        employee = getFuncionario();
        employeeDTO = getFuncionarioDTO();
        address = AddressServiceImplTest.getEndereco();
    }

    @Test
    @DisplayName("Deve salvar um funcionario com sucesso")
    void saveEmployee() {
        when(employeeRepository.existsEmployeeByCpf(employeeDTO.getCpf())).thenReturn(false);
        when(mapper.map(employeeDTO, Employee.class)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(addressService.saveAddress(AddressServiceImplTest.getEnderecoDTO())).thenReturn(address);

        Employee employeeSalvo = funcionarioService.saveEmployee(employeeDTO);

        assertNotNull(employeeSalvo);
        assertEquals(employeeDTO.getCpf(), employeeSalvo.getCpf());
        assertEquals(employeeDTO.getName(), employeeSalvo.getName());
        assertEquals(employeeDTO.getPhone(), employeeSalvo.getPhone());
        assertEquals(employee.getCreatedAt(), employeeSalvo.getCreatedAt());
        assertNotNull(employeeSalvo.getAddress());
        assertEquals(employee.getAddress(), employeeSalvo.getAddress());

        verify(employeeRepository, times(1)).existsEmployeeByCpf(employeeDTO.getCpf());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar salvar um funcionario com CPF ja cadastrado")
    void erroSaveEmployeeCpfJaCadastrado() {
        when(employeeRepository.existsEmployeeByCpf(employeeDTO.getCpf())).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            funcionarioService.saveEmployee(employeeDTO);
        });
        assertEquals("Erro ao tentar salvar Funcionário, CPF já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar uma lista de funcionarios com sucesso")
    void findAllEmployees() {
        List<Employee> listaEmployees = new ArrayList<>();
        listaEmployees.add(employee);
        listaEmployees.add(employee);
        listaEmployees.add(employee);

        when(employeeRepository.findAll()).thenReturn(listaEmployees);

        List<Employee> funcionariosRetornados = funcionarioService.findAllEmployees();

        assertNotNull(funcionariosRetornados);
        assertEquals(3, funcionariosRetornados.size());
        assertEquals(listaEmployees, funcionariosRetornados);
    }

    @Test
    @DisplayName("Deve buscar um funcionario por CPF com sucesso")
    void findEmployeeByCpf() {
        String cpfFuncionario = "123.456.789-10";

        when(employeeRepository.existsEmployeeByCpf(cpfFuncionario)).thenReturn(true);
        when(employeeRepository.findEmployeeByCpf(cpfFuncionario)).thenReturn(Optional.of(employee));

        Employee employeeBuscado = funcionarioService.findEmployeeByCpf(cpfFuncionario);

        assertNotNull(employee);
        assertEquals(employee, employeeBuscado);
        assertEquals(Employee.class, employee.getClass());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar buscar um funcionario inexistente")
    void erroFindEmployeeByCpf() {
        String cpfFuncionario = "123.456.789-10";

        when(employeeRepository.existsEmployeeByCpf(cpfFuncionario)).thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            funcionarioService.findEmployeeByCpf(cpfFuncionario);
        });
        assertEquals("Funcionário não encrontrado para o CPF informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar um funcionario com sucesso")
    void updateEmployee() {
        Employee employeeAntigo = Employee.builder()
                .cpf("109.876.543-21")
                .name("Jaelson Batista")
                .phone("(81) 91111-1111")
                .createdAt(LocalDateTime.now())
                .address(address)
                .build();
        Employee novoEmployee;

        when(employeeRepository.findEmployeeByCpf(employeeDTO.getCpf())).thenReturn(Optional.of(employeeAntigo));

        when(mapper.map(employeeDTO, Employee.class)).thenReturn(
                novoEmployee = Employee.builder()
                .cpf(employeeDTO.getCpf())
                .name(employeeDTO.getName())
                .phone(employeeDTO.getPhone())
                .createdAt(null)
                .address(address)
                .build()
        );

        when(mapper.map(employeeDTO.getAddress(), Address.class)).thenReturn(address);
        when(employeeRepository.save(novoEmployee)).thenReturn(novoEmployee);

        Employee employeeAtualizado = funcionarioService.updateEmployee(employeeDTO);

        assertNotNull(employeeAtualizado);
        assertEquals(employeeDTO.getCpf(), employeeAtualizado.getCpf());
        assertEquals(employeeDTO.getName(), employeeAtualizado.getName());
        assertEquals(employeeDTO.getPhone(), employeeAtualizado.getPhone());
        assertNotNull(employeeAtualizado.getCreatedAt());
        assertEquals(employeeAntigo.getCreatedAt(), employeeAtualizado.getCreatedAt());
        assertNotNull(employeeAtualizado.getAddress());
        assertEquals(address, employeeAtualizado.getAddress());
        assertEquals(Employee.class, employeeAtualizado.getClass());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar atualizar um funcionario que nao existe")
    void erroUpdateEmployeeInexistente() {
        when(employeeRepository.existsEmployeeByCpf(employeeDTO.getCpf())).thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            funcionarioService.updateEmployee(employeeDTO);
        });
        assertEquals("Funcionário não encrontrado para o CPF informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar um funcionario com sucesso")
    void deleteEmployee() {
        String cpfFuncionario = "123.456.789-10";

        when(employeeRepository.existsEmployeeByCpf(cpfFuncionario)).thenReturn(true);
        doNothing().when(employeeRepository).deleteEmployeeByCpf(cpfFuncionario);

        funcionarioService.deleteEmployee(cpfFuncionario);

        verify(employeeRepository, times(1)).existsEmployeeByCpf(cpfFuncionario);
        verify(employeeRepository, times(1)).deleteEmployeeByCpf(cpfFuncionario);
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um funcionario que nao existe")
    void erroDeleteEmployeeInexistente() {
        String cpfFuncionario = "123.456.789-10";

        when(employeeRepository.existsEmployeeByCpf(cpfFuncionario)).thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            funcionarioService.deleteEmployee(cpfFuncionario);
        });
        assertEquals("Funcionário não encrontrado para o CPF informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao validar um CPF ja cadastrado")
    void validateEmployeeCpfToSave() {
        String cpfFuncionario = "123.456.789-10";

        when(employeeRepository.existsEmployeeByCpf(cpfFuncionario)).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            funcionarioService.validateEmployeeCpfToSave(cpfFuncionario);
        });
        assertEquals("Erro ao tentar salvar Funcionário, CPF já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve lancar erro ao validar um CPF que ainda nao foi cadastrado")
    void validateEmployeeCpfToSaveSemErro() {
        String cpfFuncionario = "123.456.789-10";

        when(employeeRepository.existsEmployeeByCpf(cpfFuncionario)).thenReturn(false);

        assertDoesNotThrow(() -> {
            funcionarioService.validateEmployeeCpfToSave(cpfFuncionario);
        });
    }

    @Test
    @DisplayName("Deve retornar uma lista de todos funcionarios fora o funcionario informado no parametro")
    void filterEmployeesByDifferentCpf() {
        List<Employee> listaEmployees = new ArrayList<>();
        listaEmployees.add(employee);
        listaEmployees.add(employee);
        listaEmployees.add(employee);

        when(employeeRepository.findByCpfNot(employeeDTO.getCpf())).thenReturn(listaEmployees);

        List<Employee> funcionariosFiltrados =
                funcionarioService.filterEmployeesByDifferentCpf(employeeDTO);

        assertNotNull(funcionariosFiltrados);
        assertEquals(3, funcionariosFiltrados.size());
        assertEquals(ArrayList.class, funcionariosFiltrados.getClass());
    }

    @Test
    @DisplayName("Deve lancar erro porque o funcionario nao existe")
    void checkExistingEmployeeCpf() {
        String cpfFuncionario = "109.876.543-21";

        when(employeeRepository.existsEmployeeByCpf(cpfFuncionario)).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            funcionarioService.validateEmployeeCpfToSave(cpfFuncionario);
        });
        assertEquals("Erro ao tentar salvar Funcionário, CPF já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve lancar erro porque o funcionario existe")
    void checkExistingEmployeeCpfSemErro() {
        String cpfFuncionario = "109.876.543-21";

        when(employeeRepository.existsEmployeeByCpf(cpfFuncionario)).thenReturn(false);

        assertDoesNotThrow(() -> {
            funcionarioService.validateEmployeeCpfToSave(cpfFuncionario);
        });
    }

    @Test
    @DisplayName("Deve retornar true quando existir funcionario por id do endereco")
    void existsEmployeeByAddressId() {
        Integer idEndereco = 1;

        when(employeeRepository.existsEmployeeByAddressId(idEndereco)).thenReturn(true);

        assertTrue( funcionarioService.existsEmployeeByAddressId(idEndereco) );
    }

    @Test
    @DisplayName("Deve retornar false quando nao existir funcionario por id do endereco")
    void naoExistsEmployeeByAddressId() {
        Integer idEndereco = 1;

        when(employeeRepository.existsEmployeeByAddressId(idEndereco)).thenReturn(false);

        assertFalse( funcionarioService.existsEmployeeByAddressId(idEndereco) );
    }

    public static Employee getFuncionario() {
        return Employee.builder()
                .cpf("123.456.789-10")
                .name("Jabison Batista")
                .phone("(81) 91234-5678")
                .createdAt(null)
                .address(null)
                .build();
    }

    public static EmployeeDTO getFuncionarioDTO() {
        return EmployeeDTO.builder()
                .cpf("123.456.789-10")
                .name("Jabison Batista")
                .phone("(81) 91234-5678")
                .createdAt(null)
                .address(AddressServiceImplTest.getEnderecoDTO())
                .build();
    }
}