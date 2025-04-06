package com.jbmotos.model.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbmotos.model.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findEmployeeByCpf(String cpf);

    boolean existsEmployeeByCpf(String cpf);

    void deleteEmployeeByCpf(String cpf);

    boolean existsEmployeeByAddressId(Integer addressId);

    List<Employee> findByCpfNot(String cpf);
}
