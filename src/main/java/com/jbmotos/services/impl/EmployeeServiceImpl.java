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

import com.jbmotos.api.dto.EmployeeDTO;
import com.jbmotos.model.entity.Address;
import com.jbmotos.model.entity.Employee;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.model.repositories.EmployeeRepository;
import com.jbmotos.services.AddressService;
import com.jbmotos.services.EmployeeService;
import com.jbmotos.services.exception.ObjectNotFoundException;
import com.jbmotos.services.exception.BusinessRuleException;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	private static final String EMPLOYEE_NOT_FOUND_MSG = "Funcionário não encrontrado para o CPF informado.";

    private static final String ERROR_SAVE_EMPLOYEE_MSG = "Erro ao tentar salvar Funcionário";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ModelMapper mapper;

	@Override
	@Transactional
	public Employee saveEmployee(EmployeeDTO employeeDTO) {
		Address addressSaved = this.addressService.saveAddress(employeeDTO.getAddress());
		
		this.validateEmployeeCpfToSave(employeeDTO.getCpf());
		Employee employee = this.mapper.map(employeeDTO, Employee.class);
		employee.setEmployeeStatus(Situation.ACTIVE);

		employee.setAddress(addressSaved);

		return this.employeeRepository.save(employee);
	}

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAllEmployees() {
        return this.employeeRepository.findAll();
    }

	@Override
	@Transactional(readOnly = true)
	public Employee findEmployeeByCpf(String cpf) {
		return this.employeeRepository.findEmployeeByCpf(cpf)
				.orElseThrow(() -> new ObjectNotFoundException(EMPLOYEE_NOT_FOUND_MSG));
	}

    @Override
    @Transactional(readOnly = true)
    public List<Employee> filterEmployee(EmployeeDTO employeeDTO) {
        Example<Employee> example = Example.of(this.mapper.map(employeeDTO, Employee.class),
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        return this.employeeRepository.findAll(example, sort);
    }

	@Override
	@Transactional
	public Situation toggleEmployeeStatus(String cpf) {
		Employee employee = findEmployeeByCpf(cpf);
		if (employee.getEmployeeStatus().equals(Situation.ACTIVE)) {
			employee.setEmployeeStatus(Situation.INACTIVE);
		} else if (employee.getEmployeeStatus().equals(Situation.INACTIVE)) {
			employee.setEmployeeStatus(Situation.ACTIVE);
		}
		this.employeeRepository.save(employee);
		return employee.getEmployeeStatus();
	}

	@Override
	@Transactional
	public Employee updateEmployee(EmployeeDTO employeeDTO) {
		Employee employee = this.mapper.map(employeeDTO, Employee.class);

		LocalDateTime dateTime = findEmployeeByCpf(employeeDTO.getCpf()).getCreatedAt();
		employee.setCreatedAt(dateTime);

		Address address = this.mapper.map(employeeDTO.getAddress(), Address.class);
		employee.setAddress(address);

		return this.employeeRepository.save(employee);
	}

	@Override
	@Transactional
	public void deleteEmployee(String cpf) {
		this.checkExistingEmployeeCpf(cpf);
		this.employeeRepository.deleteEmployeeByCpf(cpf);
	}

	@Override
	public void checkExistingEmployeeCpf(String employeeCpf) {
		if (!this.employeeRepository.existsEmployeeByCpf(employeeCpf)) {
			throw new ObjectNotFoundException(EMPLOYEE_NOT_FOUND_MSG);
		}
	}

    @Override
    public void validateEmployeeCpfToSave(String cpf) {
        if (this.employeeRepository.existsEmployeeByCpf(cpf)) {
            throw new BusinessRuleException(ERROR_SAVE_EMPLOYEE_MSG + ", CPF já cadastrado.");
        }
    }

    @Override
    public List<Employee> filterEmployeesByDifferentCpf(EmployeeDTO employeeDTO) {
        return this.employeeRepository.findByCpfNot(employeeDTO.getCpf());
    }

    @Override
    public boolean existsEmployeeByAddressId(Integer addressId) {
        return this.employeeRepository.existsEmployeeByAddressId(addressId);
    }
}
