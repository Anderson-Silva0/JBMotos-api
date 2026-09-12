package com.jbmotos.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jbmotos.api.dto.AddressDTO;
import com.jbmotos.model.enums.Situation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import com.jbmotos.api.dto.SupplierDTO;
import com.jbmotos.model.entity.Address;
import com.jbmotos.model.entity.Supplier;
import com.jbmotos.model.repositories.SupplierRepository;
import com.jbmotos.services.AddressService;
import com.jbmotos.services.exception.BusinessRuleException;
import com.jbmotos.services.exception.ObjectNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

    @InjectMocks
    private SupplierServiceImpl fornecedorService;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private ModelMapper mapper;

    private Supplier supplier;
    private SupplierDTO supplierDTO;
    private Address address;

    @BeforeEach
    void setUp() {
        this.supplier = getFornecedor();
        this.supplierDTO = getFornecedorDTO();
        this.address = AddressServiceImplTest.getEndereco();
    }

    @Test
    @DisplayName("Deve salvar um Fornecedor com sucesso")
    void salvarFornecedor() {
        when(supplierRepository.existsSupplierByCnpj(supplierDTO.getCnpj())).thenReturn(false);
        when(mapper.map(supplierDTO, Supplier.class)).thenReturn(supplier);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        when(addressService.saveAddress(supplierDTO.getAddress())).thenReturn(address);

        Supplier supplierSalvo = fornecedorService.saveSupplier(supplierDTO);

        assertNotNull(supplierSalvo);
        assertNotNull(supplierSalvo.getAddress());
        assertEquals(supplier.getCnpj(), supplierSalvo.getCnpj());
        assertEquals(supplier.getName(), supplierSalvo.getName());
        assertEquals(supplier.getPhone(), supplierSalvo.getPhone());
        verify(supplierRepository, times(1)).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Deve retornar todos os fornecedores")
    void findAllSuppliers() {
        List<Supplier> fornecedores = List.of(supplier, supplier);
        when(supplierRepository.findAll()).thenReturn(fornecedores);

        List<Supplier> resultado = fornecedorService.findAllSuppliers();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(fornecedores, resultado);
    }

    @Test
    @DisplayName("Deve buscar um fornecedor por CNPJ com sucesso")
    void findSupplierByCnpj() {
        when(supplierRepository.findSupplierByCnpj(supplier.getCnpj())).thenReturn(Optional.of(supplier));

        Supplier resultado = fornecedorService.findSupplierByCnpj(supplier.getCnpj());

        assertNotNull(resultado);
        assertEquals(supplier, resultado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar fornecedor com CNPJ inexistente")
    void erroFindSupplierByCnpj() {
        when(supplierRepository.findSupplierByCnpj(supplier.getCnpj())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                fornecedorService.findSupplierByCnpj(supplier.getCnpj()));

        assertEquals("Fornecedor não encrontrado para o CNPJ informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar um fornecedor com sucesso")
    void updateSupplier() {
        SupplierDTO dtoAtualizado = SupplierDTO.builder()
                .cnpj(supplier.getCnpj())
                .name("Maringá LTDA")
                .phone("(81) 99123-4567")
                .createdAt(LocalDateTime.now())
                .address(AddressDTO.builder().id(2).build())
                .build();

        Supplier supplierMapeado = Supplier.builder()
                .cnpj(dtoAtualizado.getCnpj())
                .name(dtoAtualizado.getName())
                .phone(dtoAtualizado.getPhone())
                .createdAt(null)
                .address(address)
                .build();

        when(supplierRepository.findSupplierByCnpj(dtoAtualizado.getCnpj())).thenReturn(Optional.of(supplier));
        when(mapper.map(dtoAtualizado, Supplier.class)).thenReturn(supplierMapeado);
        when(mapper.map(dtoAtualizado.getAddress(), Address.class)).thenReturn(address);
        when(supplierRepository.save(supplierMapeado)).thenReturn(supplierMapeado);

        Supplier resultado = fornecedorService.updateSupplier(dtoAtualizado);

        assertNotNull(resultado);
        assertEquals(dtoAtualizado.getCnpj(), resultado.getCnpj());
        assertEquals(dtoAtualizado.getName(), resultado.getName());
        assertEquals(dtoAtualizado.getPhone(), resultado.getPhone());
        assertEquals(address, resultado.getAddress());
    }

    @Test
    @DisplayName("Deve deletar um fornecedor com sucesso")
    void deleteSupplier() {
        when(supplierRepository.existsSupplierByCnpj(supplierDTO.getCnpj())).thenReturn(true);

        fornecedorService.deleteSupplier(supplierDTO.getCnpj());

        verify(supplierRepository, times(1)).deleteSupplierByCnpj(supplierDTO.getCnpj());
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar salvar fornecedor com CNPJ já cadastrado")
    void validateSupplierCnpjToSave() {
        when(supplierRepository.existsSupplierByCnpj(supplierDTO.getCnpj())).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                fornecedorService.validateSupplierCnpjToSave(supplierDTO.getCnpj()));

        assertEquals("Erro ao tentar salvar Fornecedor, CNPJ já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve filtrar fornecedores por exemplo")
    void filtrarFornecedoresPorCnpjDiferente() {
        when(mapper.map(supplierDTO, Supplier.class)).thenReturn(supplier);
        when(supplierRepository.findAll(any(Example.class), any(Sort.class))).thenReturn(List.of(supplier));

        List<Supplier> resultado = fornecedorService.filterSupplier(supplierDTO);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(supplier, resultado.get(0));
    }

    @Test
    @DisplayName("Deve alternar o status de um fornecedor")
    void toggleSupplierStatus() {
        supplier.setSupplierStatus(Situation.ACTIVE);
        when(supplierRepository.findSupplierByCnpj(supplier.getCnpj())).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(supplier)).thenReturn(supplier);

        Situation situacao = fornecedorService.toggleSupplierStatus(supplier.getCnpj());

        assertEquals(Situation.INACTIVE, situacao);
    }

    @Test
    @DisplayName("Deve verificar se existe fornecedor para um endereço")
    void existsSupplierByAddress() {
        when(supplierRepository.existsSupplierByAddressId(address.getId())).thenReturn(true);

        boolean resultado = fornecedorService.existsSupplierByAddress(address.getId());

        assertEquals(true, resultado);
    }

    public static Supplier getFornecedor() {
        return Supplier.builder()
                .cnpj("21.300.144/0001-33")
                .name("Maringá")
                .phone("(81) 98311-0568")
                .createdAt(LocalDateTime.now())
                .address(null)
                .build();
    }

    public static SupplierDTO getFornecedorDTO() {
        return SupplierDTO.builder()
                .cnpj("21.300.144/0001-33")
                .name("Maringá")
                .phone("(81) 98311-0568")
                .createdAt(LocalDateTime.now())
                .address(AddressDTO.builder().id(1).build())
                .build();
    }
}