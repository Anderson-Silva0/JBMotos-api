package com.jbmotos.services.impl;

import com.jbmotos.api.dto.AddressDTO;
import com.jbmotos.model.entity.Address;
import com.jbmotos.model.repositories.AddressRepository;
import com.jbmotos.services.CustomerService;
import com.jbmotos.services.EmployeeService;
import com.jbmotos.services.SupplierService;
import com.jbmotos.services.exception.BusinessRuleException;
import com.jbmotos.services.exception.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @InjectMocks
    private AddressServiceImpl enderecoService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private SupplierService supplierService;

    @Mock
    private ModelMapper mapper;

    private Address address;
    private AddressDTO addressDTO;

    @BeforeEach
    void setUp() {
        this.address = getEndereco();
        this.addressDTO = getEnderecoDTO();
    }

    @Test
    @DisplayName("Deve salvar um Endereco com sucesso")
    void saveAddress() {
        when(mapper.map(any(), any())).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);

        Address addressSalvo = enderecoService.saveAddress(addressDTO);

        assertNotNull(addressSalvo);
        assertEquals(address.getId(), addressSalvo.getId());
        assertEquals(address.getRoad(), addressSalvo.getRoad());
        assertEquals(address.getCep(), addressSalvo.getCep());
        assertEquals(address.getNumber(), addressSalvo.getNumber());
        assertEquals(address.getNeighborhood(), addressSalvo.getNeighborhood());
        assertEquals(address.getCity(), addressSalvo.getCity());
    }

    @Test
    @DisplayName("Deve retornar uma lista de Enderecos")
    void findAllAddress() {
        List<Address> listaAddresses = new ArrayList<>();
        listaAddresses.add(address);
        listaAddresses.add(address);
        listaAddresses.add(address);

        when(addressRepository.findAll()).thenReturn(listaAddresses);

        List<Address> enderecosRetornados = enderecoService.findAllAddress();

        assertNotNull(enderecosRetornados);
        assertEquals(3, enderecosRetornados.size());
        assertEquals(listaAddresses, enderecosRetornados);
    }

    @Test
    @DisplayName("Deve buscar um Endereco por id com sucesso")
    void findAddressById() {
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));

        Address addressBuscado = enderecoService.findAddressById(address.getId());

        assertNotNull(addressBuscado);
        assertEquals(address, addressBuscado);
        assertEquals(Address.class, addressBuscado.getClass());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar buscar um Endereco por id")
    void erroFindAddressById() {
        when(addressRepository.findById(address.getId())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                enderecoService.findAddressById(address.getId()));
        assertEquals("Endereço não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar um Endereco com sucesso")
    void updateAddress() {
        when(addressRepository.existsById(address.getId())).thenReturn(true);
        when(mapper.map(any(), any())).thenReturn(address);
        when(addressRepository.save(any())).thenReturn(address);

        Address addressAtualizado = enderecoService.updateAddress(addressDTO);

        assertNotNull(addressAtualizado);
        assertEquals(address.getId(), addressAtualizado.getId());
        verify(addressRepository, times(1)).existsById(address.getId());
        verify(mapper, times(1)).map(any(), any());
        verify(addressRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar atualizar um Endereco")
    void erroUpdateAddress() {
        when(addressRepository.existsById(address.getId())).thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                enderecoService.updateAddress(addressDTO));
        assertEquals("Endereço não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar um Endereco com sucesso")
    void deleteAddressById() {
        when(addressRepository.existsById(address.getId())).thenReturn(true);
        doNothing().when(addressRepository).deleteById(address.getId());

        enderecoService.deleteAddressById(address.getId());

        verify(addressRepository, times(1)).deleteById(address.getId());
        verify(addressRepository, times(1)).existsById(address.getId());
        verify(customerService, times(1)).existsCustomerByAddressId(address.getId());
        verify(employeeService, times(1)).existsEmployeeByAddressId(address.getId());
        verify(supplierService, times(1)).existsSupplierByAddress(address.getId());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um Endereco que nao existe")
    void erroDeletarEnderecoInexistente() {
        when(addressRepository.existsById(address.getId())).thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                enderecoService.deleteAddressById(address.getId()));
        assertEquals("Endereço não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um Endereco que está sendo usado por um Cliente")
    void erroDeletarEnderecoEmUsoPorUmCliente() {
        when(addressRepository.existsById(address.getId())).thenReturn(true);
        when(customerService.existsCustomerByAddressId(address.getId())).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                enderecoService.deleteAddressById(address.getId()));
        assertEquals("Erro ao tentar deletar, o Endereço pertence a um Cliente.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um Endereco que está sendo usado por um Funcionario")
    void erroDeletarEnderecoEmUsoPorUmFuncionario() {
        when(addressRepository.existsById(address.getId())).thenReturn(true);
        when(employeeService.existsEmployeeByAddressId(address.getId())).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                enderecoService.deleteAddressById(address.getId()));
        assertEquals("Erro ao tentar deletar, o Endereço pertence a um Funcionário.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um Endereco que está sendo usado por um Fornecedor")
    void erroDeletarEnderecoEmUsoPorUmFornecedor() {
        when(addressRepository.existsById(address.getId())).thenReturn(true);
        when(supplierService.existsSupplierByAddress(address.getId())).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                enderecoService.deleteAddressById(address.getId()));
        assertEquals("Erro ao tentar deletar, o Endereço pertence a um Fornecedor.", exception.getMessage());
    }

    @Test
    void validateAddressSemErro() {
        when(addressRepository.existsById(anyInt())).thenReturn(true);

        assertDoesNotThrow(() -> enderecoService.validateAddress(1));
        verify(addressRepository).existsById(1);
    }

    @Test
    void validateAddressComErro() {
        when(addressRepository.existsById(anyInt())).thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                enderecoService.validateAddress(1));
        assertEquals("Endereço não encontrado para o Id informado.", exception.getMessage());
    }

    public static Address getEndereco() {
        return Address.builder()
                .id(1)
                .road("Rua flores do oriente")
                .cep("51250-545")
                .number(100)
                .neighborhood("Jordão Baixo")
                .city("Recife")
                .build();
    }

    public static AddressDTO getEnderecoDTO() {
        return AddressDTO.builder()
                .id(1)
                .road("Rua flores do oriente")
                .cep("51250-545")
                .number(100)
                .neighborhood("Jordão Baixo")
                .city("Recife")
                .build();
    }
}