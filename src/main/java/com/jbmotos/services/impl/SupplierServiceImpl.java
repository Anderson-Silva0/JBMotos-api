package com.jbmotos.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.SupplierDTO;
import com.jbmotos.model.entity.Address;
import com.jbmotos.model.entity.Supplier;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.model.repositories.SupplierRepository;
import com.jbmotos.services.AddressService;
import com.jbmotos.services.SupplierService;
import com.jbmotos.services.exception.ObjectNotFoundException;
import com.jbmotos.services.exception.BusinessRuleException;

@Service
public class SupplierServiceImpl implements SupplierService {

	private static final String SUPPLIER_NOT_FOUND_MSG = "Fornecedor não encrontrado para o CNPJ informado.";

    private static final String ERROR_SAVE_SUPPLIER_MSG = "Erro ao tentar salvar Fornecedor";

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ModelMapper mapper;

	@Override
	@Transactional
	public Supplier saveSupplier(SupplierDTO supplierDTO) {
		Address addressSaved = this.addressService.saveAddress(supplierDTO.getAddress());
		
		this.validateSupplierCnpjToSave(supplierDTO.getCnpj());
		Supplier supplier = this.mapper.map(supplierDTO, Supplier.class);
		supplier.setSupplierStatus(Situation.ACTIVE);

		supplier.setAddress(addressSaved);

		return this.supplierRepository.save(supplier);
	}

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> findAllSuppliers() {
        return this.supplierRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Supplier findSupplierByCnpj(String cnpj) {
        return this.supplierRepository.findSupplierByCnpj(cnpj)
        		.orElseThrow(() -> new ObjectNotFoundException(SUPPLIER_NOT_FOUND_MSG));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> filterSupplier(SupplierDTO supplierDTO) {
        Example<Supplier> example = Example.of(this.mapper.map(supplierDTO, Supplier.class),
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        return this.supplierRepository.findAll(example, sort);
    }

	@Override
	@Transactional
	public Situation toggleSupplierStatus(String cnpj) {
		Supplier supplier = findSupplierByCnpj(cnpj);
		if (supplier.getSupplierStatus().equals(Situation.ACTIVE)) {
			supplier.setSupplierStatus(Situation.INACTIVE);
		} else if (supplier.getSupplierStatus().equals(Situation.INACTIVE)) {
			supplier.setSupplierStatus(Situation.ACTIVE);
		}
		this.supplierRepository.save(supplier);
		return supplier.getSupplierStatus();
	}

	@Override
	@Transactional
	public Supplier updateSupplier(SupplierDTO supplierDTO) {
		Supplier supplier = this.mapper.map(supplierDTO, Supplier.class);

		LocalDateTime dateTime = findSupplierByCnpj(supplierDTO.getCnpj()).getCreatedAt();
		supplier.setCreatedAt(dateTime);

		Address address = this.mapper.map(supplierDTO.getAddress(), Address.class);
		supplier.setAddress(address);

		return this.supplierRepository.save(supplier);
	}

	@Override
	@Transactional
	public void deleteSupplier(String cnpj) {
		if (!this.supplierRepository.existsSupplierByCnpj(cnpj)) {
			throw new ObjectNotFoundException(SUPPLIER_NOT_FOUND_MSG);
		}
		this.supplierRepository.deleteSupplierByCnpj(cnpj);
	}

    @Override
    public void validateSupplierCnpjToSave(String cnpj) {
        if (this.supplierRepository.existsSupplierByCnpj(cnpj)) {
            throw new BusinessRuleException(ERROR_SAVE_SUPPLIER_MSG + ", CNPJ já cadastrado.");
        }
    }

    @Override
    public boolean existsSupplierByAddress(Integer addressId) {
        return this.supplierRepository.existsSupplierByAddressId(addressId);
    }
}
