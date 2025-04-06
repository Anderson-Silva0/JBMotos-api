package com.jbmotos.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.AddressDTO;
import com.jbmotos.model.entity.Address;
import com.jbmotos.model.repositories.AddressRepository;
import com.jbmotos.services.CustomerService;
import com.jbmotos.services.AddressService;
import com.jbmotos.services.SupplierService;
import com.jbmotos.services.EmployeeService;
import com.jbmotos.services.exception.ObjectNotFoundException;
import com.jbmotos.services.exception.BusinessRuleException;

@Service
public class AddressServiceImpl implements AddressService {

	private static final String ADDRESS_NOT_FOUND_MSG = "Endereço não encontrado para o Id informado.";

    private static final String ERROR_DELETE_ADDRESS_MSG = "Erro ao tentar deletar, o Endereço pertence a um";

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    @Lazy
    private CustomerService customerService;

    @Autowired
    @Lazy
    private EmployeeService employeeService;

    @Autowired
    @Lazy
    private SupplierService supplierService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Address saveAddress(AddressDTO addressDTO) {
        return this.addressRepository.save(this.mapper.map(addressDTO, Address.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Address> findAllAddress() {
        return this.addressRepository.findAll();
    }

	@Override
	@Transactional(readOnly = true)
	public Address findAddressById(Integer id) {
		return this.addressRepository.findById(id)
				.orElseThrow(() -> new ObjectNotFoundException(ADDRESS_NOT_FOUND_MSG));
	}

	@Override
	@Transactional
	public Address updateAddress(AddressDTO addressDTO) {
		this.validateAddress(addressDTO.getId());
		return this.addressRepository.save(this.mapper.map(addressDTO, Address.class));
	}

	@Override
	@Transactional
	public void deleteAddressById(Integer id) {
		this.validateAddress(id);
		this.checkUsageAddress(id);
		this.addressRepository.deleteById(id);
	}

	@Override
	public void validateAddress(Integer id) {
		if (!addressRepository.existsById(id)) {
			throw new ObjectNotFoundException(ADDRESS_NOT_FOUND_MSG);
		}
	}

	private void checkUsageAddress(Integer addressId) {
		if (this.customerService.existsCustomerByAddressId(addressId)) {
			throw new BusinessRuleException(ERROR_DELETE_ADDRESS_MSG + " Cliente.");
		}
		if (this.employeeService.existsEmployeeByAddressId(addressId)) {
			throw new BusinessRuleException(ERROR_DELETE_ADDRESS_MSG + " Funcionário.");
		}
		if (this.supplierService.existsSupplierByAddress(addressId)) {
			throw new BusinessRuleException(ERROR_DELETE_ADDRESS_MSG + " Fornecedor.");
		}
	}
}
