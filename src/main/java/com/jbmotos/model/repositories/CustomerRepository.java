package com.jbmotos.model.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbmotos.model.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {

    Optional<Customer> findCustomerByCpf(String cpf);

    boolean existsCustomerByCpf(String cpf);

    boolean existsCustomerByEmail(String email);

    void deleteCustomerByCpf(String cpf);

    boolean existsCustomerByAddressId(Integer addressId);

    List<Customer> findByCpfNot(String cpf);
}
