package com.jbmotos.services.impl;

import java.util.List;

import com.jbmotos.api.dto.EmployeeDTO;
import com.jbmotos.model.entity.Motorcycle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.RepairDTO;
import com.jbmotos.api.dto.SaleDTO;
import com.jbmotos.model.entity.Employee;
import com.jbmotos.model.entity.Repair;
import com.jbmotos.model.entity.Sale;
import com.jbmotos.model.repositories.RepairRepository;
import com.jbmotos.services.EmployeeService;
import com.jbmotos.services.MotorcycleService;
import com.jbmotos.services.RepairService;
import com.jbmotos.services.SaleService;
import com.jbmotos.services.exception.ObjectNotFoundException;
import com.jbmotos.services.exception.BusinessRuleException;

@Service
public class RepairServiceImpl implements RepairService {

    @Autowired
    private RepairRepository repairRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private MotorcycleService motorcycleService;

    @Autowired
    private SaleService saleService;
    
    @Autowired
    private ModelMapper mapper;

	@Override
	@Transactional
	public Repair saveRepair(RepairDTO repairDTO) {
		Repair repair = this.mapper.map(repairDTO, Repair.class);
		Sale saleSaved = null;
		SaleDTO saleDTO = repairDTO.getSale();
		
		if (saleDTO != null) {
			saleSaved = this.saleService.saveSale(saleDTO);
		}

		EmployeeDTO employeeDTO = repairDTO.getEmployee();
		if (employeeDTO != null) {
			Employee employee = this.employeeService.findEmployeeByCpf(employeeDTO.getCpf());
			repair.setEmployee(employee);
		}

		Motorcycle motorcycle = this.motorcycleService.findMotorcycleById(repairDTO.getMotorcycle().getId());
		repair.setMotorcycle(motorcycle);

		repair.setSale(saleSaved);

		return this.repairRepository.save(repair);
	}

    @Override
    @Transactional(readOnly = true)
    public List<Repair> findAllRepairs() {
        return this.repairRepository.findAll();
    }

	@Override
	@Transactional(readOnly = true)
	public Repair findRepairById(Integer repairId) {
		return this.repairRepository.findById(repairId)
				.orElseThrow(() -> new ObjectNotFoundException("Serviço não encontrado para o Id informado."));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Repair> filterRepair(RepairDTO repairDTO) {
		Example<Repair> example = Example.of(this.mapper.map(repairDTO, Repair.class),
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

		return this.repairRepository.findAll(example, sort);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Repair findRepairBySaleId(Integer saleId) {
		this.saleService.validateSale(saleId);
		return this.repairRepository.findRepairBySaleId(saleId)
				.orElseThrow(() -> new BusinessRuleException("A Venda informada não pertence a um Serviço."));
	}

    @Override
    @Transactional(readOnly = true)
    public List<Repair> findRepairByEmployeeCpf(String employeeCpf) {
        this.employeeService.checkExistingEmployeeCpf(employeeCpf);
        return this.repairRepository.findRepairByEmployeeCpf(employeeCpf);
    }

	@Override
	@Transactional
	public Repair updateRepair(RepairDTO repairDTO) {
		Repair newRepair = this.mapper.map(repairDTO, Repair.class);

		Repair oldRepair = findRepairById(repairDTO.getId());
		newRepair.setCreatedAt(oldRepair.getCreatedAt());

		EmployeeDTO employeeDTO = repairDTO.getEmployee();
		if (employeeDTO != null) {
			Employee employee = this.employeeService.findEmployeeByCpf(employeeDTO.getCpf());
			newRepair.setEmployee(employee);
		}

		Motorcycle motorcycle = this.motorcycleService.findMotorcycleById(repairDTO.getMotorcycle().getId());
		newRepair.setMotorcycle(motorcycle);

		newRepair.setSale(oldRepair.getSale());

		return this.repairRepository.save(newRepair);
	}

    @Override
    @Transactional
    public void deleteRepair(Integer repairId) {
        this.validateRepair(repairId);
        this.repairRepository.deleteById(repairId);
    }

    @Override
    public void validateRepair(Integer repairId) {
        if (!this.repairRepository.existsById(repairId)) {
            throw new ObjectNotFoundException("Serviço não encontrado para o Id informado.");
        }
    }
}
