package com.jbmotos.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jbmotos.api.dto.AddressDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.jbmotos.api.dto.SupplierDTO;
import com.jbmotos.model.entity.Address;
import com.jbmotos.model.entity.Supplier;
import com.jbmotos.model.repositories.SupplierRepository;
import com.jbmotos.services.AddressService;

@SpringBootTest
class SupplierServiceImplTest {

    @Autowired
    private SupplierServiceImpl fornecedorService;

    @MockBean
    private SupplierRepository supplierRepository;

    @MockBean
    private AddressService addressService;

    @MockBean
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
        // Cenário
        when(supplierRepository.existsSupplierByCnpj(supplierDTO.getCnpj())).thenReturn(false);
        when(mapper.map(supplierDTO, Supplier.class)).thenReturn(supplier);
        when(addressService.findAddressById(supplierDTO.getAddress().getId())).thenReturn(address);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        when(addressService.saveAddress(supplierDTO.getAddress())).thenReturn(address);

        // Execução
        Supplier supplierSalvo = fornecedorService.saveSupplier(supplierDTO);

        //Verificação
        assertNotNull(supplierSalvo);
        assertNotNull(supplierSalvo.getAddress());
        assertEquals(supplier.getCnpj(), supplierSalvo.getCnpj());
        assertEquals(supplier.getName(), supplierSalvo.getName());
        assertEquals(supplier.getPhone(), supplierSalvo.getPhone());
        verify(supplierRepository, times(1)).save(any(Supplier.class));
    }

    @Test
    void findAllSuppliers() {
    }

    @Test
    void findSupplierByCnpj() {
    }

    @Test
    void updateSupplier() {
    }

    @Test
    void deleteSupplier() {
    }

    @Test
    void validateSupplierCnpjToSave() {
    }

    @Test
    void filtrarFornecedoresPorCnpjDiferente() {
    }

    @Test
    void checarCnpjFornecedorExistente() {
    }

    @Test
    void existsSupplierByAddress() {
    }

    public static Supplier getFornecedor() {
        return Supplier.builder()
                .cnpj("21.300.144/0001-33")
                .name("Maringá")
                .phone("(81) 98311-0568")
                .createdAt(null)
                .address(null)
                .build();
    }

    public static SupplierDTO getFornecedorDTO() {
        return SupplierDTO.builder()
                .cnpj("21.300.144/0001-33")
                .name("Maringá")
                .phone("(81) 98311-0568")
                .createdAt(null)
                .address(AddressDTO.builder().id(1).build())
                .build();
    }
}