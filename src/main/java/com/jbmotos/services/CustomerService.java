package com.jbmotos.services;

import java.util.List;

import com.jbmotos.api.dto.CustomerDTO;
import com.jbmotos.model.entity.Customer;
import com.jbmotos.model.enums.Situation;

public interface CustomerService {

    Customer saveCustomer(CustomerDTO customerDTO);

    List<Customer> findAllCustomer();

    Customer findCustomerByCpf(String cpf);

    List<Customer> filterCustomer(CustomerDTO customerDTO);

    Situation toggleCustomerStatus(String cpf);

    Customer updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(String cpf);

    void validateEmailToSave(String email);

    void validateCustomerCpfToSave(String cpf);

    void validateEmailToUpdate(CustomerDTO customerDTO);

    boolean existsCustomerByAddressId(Integer addressId);

	void checkExistingCustomerCpf(String cpf);
}
