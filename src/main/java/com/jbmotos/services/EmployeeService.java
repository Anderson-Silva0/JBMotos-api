package com.jbmotos.services;

import java.util.List;

import com.jbmotos.api.dto.EmployeeDTO;
import com.jbmotos.model.entity.Employee;
import com.jbmotos.model.enums.Situation;

public interface EmployeeService {

    Employee saveEmployee(EmployeeDTO employeeDTO);

    List<Employee> findAllEmployees();

    Employee findEmployeeByCpf(String cpf);

    List<Employee> filterEmployee(EmployeeDTO employeeDTO);

    Situation toggleEmployeeStatus(String cpf);

    Employee updateEmployee(EmployeeDTO employeeDTO);

    void deleteEmployee(String cpf);

    void validateEmployeeCpfToSave(String cpf);

    List<Employee> filterEmployeesByDifferentCpf(EmployeeDTO employeeDTO);

    boolean existsEmployeeByAddressId(Integer addressId);

    void checkExistingEmployeeCpf(String employeeCpf);
}
