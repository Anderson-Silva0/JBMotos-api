package com.jbmotos.services.impl;

import com.jbmotos.api.dto.CustomerDTO;
import com.jbmotos.api.dto.AddressDTO;
import com.jbmotos.model.entity.Customer;
import com.jbmotos.model.entity.Address;
import com.jbmotos.model.repositories.CustomerRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CustomerServiceImplTest {

    @Autowired
    private CustomerServiceImpl clienteService;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private AddressService addressService;

    @MockBean
    private ModelMapper mapper;

    private CustomerDTO customerDTO;
    private Customer customer;
    private Address address;

    @BeforeEach
    public void setUp() {
        customerDTO = getClienteDTO();
        customer = getCliente();
        address = AddressServiceImplTest.getEndereco();
    }

    @Test
    @DisplayName("Deve salvar um cliente com sucesso")
    void saveCustomer() {
        when(customerRepository.existsCustomerByCpf(customerDTO.getCpf())).thenReturn(false);
        when(customerRepository.existsCustomerByEmail(customerDTO.getEmail())).thenReturn(false);
        when(mapper.map(customerDTO, Customer.class)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(addressService.saveAddress(customerDTO.getAddress())).thenReturn(address);

        Customer customerSalvo = clienteService.saveCustomer(customerDTO);

        assertNotNull(customerSalvo);
        assertEquals(customerDTO.getCpf(), customerSalvo.getCpf());
        assertEquals(customerDTO.getName(), customerSalvo.getName());
        assertEquals(customerDTO.getEmail(), customerSalvo.getEmail());
        assertEquals(customerDTO.getPhone(), customerSalvo.getPhone());
        assertEquals(customer.getCreatedAt(), customerSalvo.getCreatedAt());
        assertNotNull(customerSalvo.getAddress());
        assertEquals(address, customerSalvo.getAddress());

        verify(customerRepository, times(1)).existsCustomerByCpf(anyString());
        verify(customerRepository, times(1)).existsCustomerByEmail(anyString());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar salvar cliente com CPF ja cadastrado")
    void erroSaveCustomerComCpfJaCadastrado() {
        when(customerRepository.existsCustomerByCpf(customerDTO.getCpf())).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            clienteService.saveCustomer(customerDTO);
        });
        assertEquals("Erro ao tentar salvar Cliente, CPF já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar salvar cliente com email ja cadastrado")
    void erroSaveCustomerComEmailJaCadastrado() {
        when(customerRepository.existsCustomerByEmail(customerDTO.getEmail())).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            clienteService.saveCustomer(customerDTO);
        });
        assertEquals("Erro ao tentar salvar Cliente, Email já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retonar uma lista de clientes com sucesso")
    void findAllCustomer() {
        List<Customer> listaCustomers = new ArrayList<>();
        listaCustomers.add(customer);
        listaCustomers.add(customer);
        listaCustomers.add(customer);

        when(customerRepository.findAll()).thenReturn(listaCustomers);

        List<Customer> clientesRetornados = clienteService.findAllCustomer();

        assertNotNull(clientesRetornados);
        assertEquals(3, clientesRetornados.size());
        assertEquals(listaCustomers, clientesRetornados);
    }

    @Test
    @DisplayName("Deve buscar um cliente por CPF com sucesso")
    void findCustomerByCpf() {
        String cpfCliente = "123.456.789-10";

        when(customerRepository.existsCustomerByCpf(cpfCliente)).thenReturn(true);
        when(customerRepository.findCustomerByCpf(cpfCliente)).thenReturn(Optional.of(customer));

        Customer customerBuscado = clienteService.findCustomerByCpf(cpfCliente);

        assertNotNull(customerBuscado);
        assertEquals(customer, customerBuscado);
        assertEquals(Customer.class, customerBuscado.getClass());
    }

    @Test
    @DisplayName("Deve atualizar um cliente com sucesso")
    void updateCustomer() {
        Customer customerAntigo = Customer.builder()
                .cpf("123.456.789-10")
                .name("Anderson da Silva")
                .email("anderson@gmail.com")
                .phone("(81) 91234-5678")
                .createdAt(LocalDateTime.now())
                .address(address)
                .build();
        Customer novoCustomer;

        when(customerRepository.findCustomerByCpf(customerDTO.getCpf())).thenReturn(Optional.of(customerAntigo));

        when(mapper.map(customerDTO, Customer.class)).thenReturn(
                novoCustomer = Customer.builder()
                        .cpf(customerDTO.getCpf())
                        .name(customerDTO.getName())
                        .email(customerDTO.getEmail())
                        .phone(customerDTO.getPhone())
                        .createdAt(null)
                        .address(address)
                        .build()
        );

        when(mapper.map(customerDTO.getAddress(), Address.class)).thenReturn(address);
        when(customerRepository.save(novoCustomer)).thenReturn(novoCustomer);

        Customer customerAtualizado = clienteService.updateCustomer(customerDTO);

        assertNotNull(customerAtualizado);
        assertEquals(customerDTO.getCpf(), customerAtualizado.getCpf());
        assertEquals(customerDTO.getName(), customerAtualizado.getName());
        assertEquals(customerDTO.getEmail(), customerAtualizado.getEmail());
        assertEquals(customerDTO.getPhone(), customerAtualizado.getPhone());
        assertNotNull(customerAtualizado.getCreatedAt());
        assertEquals(customerAntigo.getCreatedAt(), customerAtualizado.getCreatedAt());
        assertNotNull(customerAtualizado.getAddress());
        assertEquals(address, customerAtualizado.getAddress());
        assertEquals(Customer.class, customerAtualizado.getClass());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar atualizar um cliente que nao existe")
    void erroUpdateCustomerInexistente() {
        when(customerRepository.existsCustomerByCpf(customerDTO.getCpf())).thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            clienteService.updateCustomer(customerDTO);
        });
        assertEquals("Cliente não encrontrado para o CPF informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar atualizar cliente com email ja utilizado por outro cliente")
    void erroUpdateCustomerEmailJaUtilizado() {
    	when(mapper.map(any(), any())).thenReturn(customer);
    	when(customerRepository.findCustomerByCpf(customerDTO.getCpf())).thenReturn(Optional.of(getCliente()));
        when(customerRepository.findByCpfNot(customerDTO.getCpf())).thenReturn(List.of(getCliente(), getCliente()));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            clienteService.updateCustomer(customerDTO);
        });
        assertEquals("Erro ao tentar atualizar Cliente, Email já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar um cliente com sucesso")
    void deleteCustomer() {
        String cpfCliente = "123.456.789-10";

        when(customerRepository.existsCustomerByCpf(cpfCliente)).thenReturn(true);
        doNothing().when(customerRepository).deleteCustomerByCpf(cpfCliente);

        clienteService.deleteCustomer(cpfCliente);

        verify(customerRepository, times(1)).deleteCustomerByCpf(cpfCliente);
        verify(customerRepository, times(1)).existsCustomerByCpf(cpfCliente);
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um cliente que nao existe")
    void erroDeleteCustomerInexistente() {
        String cpfCliente = "123.456.789-10";

        when(customerRepository.existsCustomerByCpf(cpfCliente)).thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            clienteService.deleteCustomer(cpfCliente);
        });
        assertEquals("Cliente não encrontrado para o CPF informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao validar um email ja cadastrado")
    void validateEmailToSave() {
        String email = "anderson@gmail.com";

        when(customerRepository.existsCustomerByEmail(email)).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            clienteService.validateEmailToSave(email);
        });
        assertEquals("Erro ao tentar salvar Cliente, Email já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve lancar erro ao validar um email que ainda nao foi cadastrado")
    void validateEmailToSaveSemErro() {
        String email = "anderson@gmail.com";

        when(customerRepository.existsCustomerByEmail(email)).thenReturn(false);

        assertDoesNotThrow(() -> {
            clienteService.validateEmailToSave(email);
        });
    }

    @Test
    @DisplayName("Deve lancar erro ao validar cpf ja cadastrado")
    void validateCustomerCpfToSave() {
        String cpf = "123.456.789-10";

        when(customerRepository.existsCustomerByCpf(cpf)).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            clienteService.validateCustomerCpfToSave(cpf);
        });
        assertEquals("Erro ao tentar salvar Cliente, CPF já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve lancar erro ao validar cpf que nao foi cadastrado")
    void validateCustomerCpfToSaveSemErro() {
        String cpf = "123.456.789-10";

        when(customerRepository.existsCustomerByCpf(cpf)).thenReturn(false);

        assertDoesNotThrow(() -> {
            clienteService.validateCustomerCpfToSave(cpf);
        });
    }

    @Test
    @DisplayName("Deve lancar erro porque algum outro cliente ja possui email igual")
    void validateEmailToUpdate() {
        List<Customer> listaCustomers = new ArrayList<>();
        listaCustomers.add(customer);
        listaCustomers.add(customer);

        when(customerRepository.findByCpfNot(customerDTO.getCpf())).thenReturn(listaCustomers);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            clienteService.validateEmailToUpdate(customerDTO);
        });
        assertEquals("Erro ao tentar atualizar Cliente, Email já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve lancar erro porque nenhum cliente possui email igual ao informado")
    void validateEmailToUpdateSemErro() {
        List<Customer> listaCustomers = new ArrayList<>();
        Customer customer1 = customer;
        customer1.setEmail("andre@gmail.com");
        Customer customer2 = customer;
        customer2.setEmail("adriano@gmail.com");
        listaCustomers.add(customer1);
        listaCustomers.add(customer2);

        when(customerRepository.findByCpfNot(customerDTO.getCpf())).thenReturn(listaCustomers);

        assertDoesNotThrow(() -> {
            clienteService.validateEmailToUpdate(customerDTO);
        });
    }

    @Test
    @DisplayName("Deve lancar erro porque o cpf nao existe no banco de dados")
    void checkExistingCustomerCpf() {
        String cpf = "123.456.789-10";

        when(customerRepository.existsCustomerByCpf(cpf)).thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            clienteService.checkExistingCustomerCpf(cpf);
        });
        assertEquals("Cliente não encrontrado para o CPF informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve lancar erro porque o cpf existe no banco de dados")
    void checkExistingCustomerCpfSemErro() {
        String cpf = "123.456.789-10";

        when(customerRepository.existsCustomerByCpf(cpf)).thenReturn(true);

        assertDoesNotThrow(() -> {
            clienteService.checkExistingCustomerCpf(cpf);
        });
    }

    @Test
    @DisplayName("Deve retornar true quando existir cliente por id do endereco")
    void existsCustomerByAddressId() {
        Integer idEndereco = 1;

        when(customerRepository.existsCustomerByAddressId(idEndereco)).thenReturn(true);

        assertTrue( clienteService.existsCustomerByAddressId(idEndereco) );
    }

    @Test
    @DisplayName("Deve retornar false quando não existir cliente por id do endereco")
    void naoExistsCustomerByAddressId() {
        Integer idEndereco = 1;

        when(customerRepository.existsCustomerByAddressId(idEndereco)).thenReturn(false);

        assertFalse( clienteService.existsCustomerByAddressId(idEndereco) );
    }

    public static Customer getCliente() {
        return Customer.builder()
                .cpf("710.606.394-08")
                .name("Anderson")
                .email("anderson@gmail.com")
                .phone("(81) 992389161")
                .createdAt(null)
                .address(null)
                .build();
    }

    public static CustomerDTO getClienteDTO() {
        return CustomerDTO.builder()
                .cpf("710.606.394-08")
                .name("Anderson")
                .email("anderson@gmail.com")
                .phone("(81) 992389161")
                .createdAt(null)
                .address(AddressDTO.builder().id(123).build())
                .build();
    }
}