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

import com.jbmotos.api.dto.CustomerDTO;
import com.jbmotos.model.entity.Customer;
import com.jbmotos.model.entity.Address;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.model.repositories.CustomerRepository;
import com.jbmotos.services.CustomerService;
import com.jbmotos.services.AddressService;
import com.jbmotos.services.exception.ObjectNotFoundException;
import com.jbmotos.services.exception.BusinessRuleException;

@Service
public class CustomerServiceImpl implements CustomerService {

	private static final String CUSTOMER_NOT_FOUND_MSG = "Cliente não encrontrado para o CPF informado.";

    private static final String ERROR_SAVE_CUSTOMER_MSG = "Erro ao tentar salvar Cliente";

    private static final String ERROR_UPDATE_CUSTOMER_MSG = "Erro ao tentar atualizar Cliente";

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ModelMapper mapper;

	@Override
	@Transactional
	public Customer saveCustomer(CustomerDTO customerDTO) {
		Address addressSaved = this.addressService.saveAddress(customerDTO.getAddress());
		
		this.validateCustomerCpfToSave(customerDTO.getCpf());
		this.validateEmailToSave(customerDTO.getEmail());
		Customer customer = this.mapper.map(customerDTO, Customer.class);
		customer.setCustomerStatus(Situation.ACTIVE);

		customer.setAddress(addressSaved);

		return this.customerRepository.save(customer);
	}

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAllCustomer() {
        return this.customerRepository.findAll();
    }

	@Override
	@Transactional(readOnly = true)
	public Customer findCustomerByCpf(String cpf) {
		return this.customerRepository.findCustomerByCpf(cpf)
				.orElseThrow(() -> new ObjectNotFoundException(CUSTOMER_NOT_FOUND_MSG));
	}

    @Override
    @Transactional(readOnly = true)
    public List<Customer> filterCustomer(CustomerDTO customerDTO) {
        Example<Customer> example = Example.of(this.mapper.map(customerDTO, Customer.class),
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        return this.customerRepository.findAll(example, sort);
    }

	@Override
	@Transactional
	public Situation toggleCustomerStatus(String cpf) {
		Customer customer = findCustomerByCpf(cpf);
		if (customer.getCustomerStatus().equals(Situation.ACTIVE)) {
			customer.setCustomerStatus(Situation.INACTIVE);
		} else if (customer.getCustomerStatus().equals(Situation.INACTIVE)) {
			customer.setCustomerStatus(Situation.ACTIVE);
		}
		this.customerRepository.save(customer);
		return customer.getCustomerStatus();
	}

	@Override
	@Transactional
	public Customer updateCustomer(CustomerDTO customerDTO) {
		Customer customer = this.mapper.map(customerDTO, Customer.class);

		LocalDateTime dateTime = findCustomerByCpf(customerDTO.getCpf()).getCreatedAt();
		customer.setCreatedAt(dateTime);

		this.validateEmailToUpdate(customerDTO);

		Address address = this.mapper.map(customerDTO.getAddress(), Address.class);
		customer.setAddress(address);

		return this.customerRepository.save(customer);
	}

	@Override
	@Transactional
	public void deleteCustomer(String cpf) {
		this.checkExistingCustomerCpf(cpf);
		this.customerRepository.deleteCustomerByCpf(cpf);
	}

	@Override
	public void checkExistingCustomerCpf(String cpf) {
		if (!this.customerRepository.existsCustomerByCpf(cpf)) {
			throw new ObjectNotFoundException(CUSTOMER_NOT_FOUND_MSG);
		}
	}

    @Override
    public void validateEmailToSave(String email) {
        if (this.customerRepository.existsCustomerByEmail(email)) {
            throw new BusinessRuleException(ERROR_SAVE_CUSTOMER_MSG +", Email já cadastrado.");
        }
    }

    @Override
    public void validateCustomerCpfToSave(String cpf) {
        if (this.customerRepository.existsCustomerByCpf(cpf)) {
            throw new BusinessRuleException(ERROR_SAVE_CUSTOMER_MSG +", CPF já cadastrado.");
        }
    }

    @Override
    public void validateEmailToUpdate(CustomerDTO customerDTO) {
        this.filterCustomersByDifferentCpf(customerDTO).forEach(filteredCustomer -> {
            if (customerDTO.getEmail().equals(filteredCustomer.getEmail())) {
                throw new BusinessRuleException(ERROR_UPDATE_CUSTOMER_MSG +", Email já cadastrado.");
            }
        });
    }

    private List<Customer> filterCustomersByDifferentCpf(CustomerDTO customerDTO) {
        return this.customerRepository.findByCpfNot(customerDTO.getCpf());
    }

    @Override
    public boolean existsCustomerByAddressId(Integer addressId) {
        return this.customerRepository.existsCustomerByAddressId(addressId);
    }
}
