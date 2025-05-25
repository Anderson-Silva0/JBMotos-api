package com.jbmotos.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.jbmotos.api.dto.CustomerDTO;
import com.jbmotos.model.entity.Motorcycle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.MotorcycleDTO;
import com.jbmotos.model.entity.Customer;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.model.repositories.MotorcycleRepository;
import com.jbmotos.services.CustomerService;
import com.jbmotos.services.MotorcycleService;
import com.jbmotos.services.exception.ObjectNotFoundException;
import com.jbmotos.services.exception.BusinessRuleException;

@Service
public class MotorcycleServiceImpl implements MotorcycleService {

	private static final String MOTORCYCLE_NOT_FOUND_ID_MSG = "Moto não encontrada para o Id informado.";
	private static final String MOTORCYCLE_NOT_FOUND_PLATE_MSG = "Moto não encontrada para a Placa informada.";

    @Autowired
    private MotorcycleRepository motorcycleRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Motorcycle saveMotorcycle(MotorcycleDTO motorcycleDTO) {
        Motorcycle motorcycle = this.mapper.map(motorcycleDTO, Motorcycle.class);
        motorcycle.setPlate(motorcycleDTO.getPlate().toUpperCase());
        validateMotorcyclePlateToSave(motorcycleDTO.getPlate());
        motorcycle.setMotorcycleStatus(Situation.ACTIVE);

        CustomerDTO customerDTO = motorcycleDTO.getCustomer();
        if (customerDTO != null) {
            Customer customer = this.customerService.findCustomerByCpf(customerDTO.getCpf());
            motorcycle.setCustomer(customer);
        }

        return this.motorcycleRepository.save(motorcycle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Motorcycle> findAllMotorcycles() {
        return this.motorcycleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Motorcycle> findMotorcycleByCustomerCpf(String customerCpf) {
        this.customerService.checkExistingCustomerCpf(customerCpf);
        this.existsMotorcycleByCustomerCpf(customerCpf);
        return this.motorcycleRepository.findMotorcyclesByCustomerCpf(customerCpf);
    }

	@Override
	@Transactional(readOnly = true)
	public Motorcycle findMotorcycleById(Integer motorcycleId) {
		return this.motorcycleRepository.findById(motorcycleId)
				.orElseThrow(() -> new ObjectNotFoundException(MOTORCYCLE_NOT_FOUND_ID_MSG));
	}

	@Override
	@Transactional(readOnly = true)
	public Motorcycle findMotorcycleByPlate(String plate) {
		String plateWithCapitalLetters = plate.toUpperCase();
		return this.motorcycleRepository.findMotorcycleByPlate(plateWithCapitalLetters)
				.orElseThrow(() -> new ObjectNotFoundException(MOTORCYCLE_NOT_FOUND_PLATE_MSG));
	}

    @Override
    @Transactional(readOnly = true)
    public List<Motorcycle> filterMotorcycle(MotorcycleDTO motorcycleDTO) {
        Example<Motorcycle> example = Example.of(this.mapper.map(motorcycleDTO, Motorcycle.class),
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        return this.motorcycleRepository.findAll(example, sort);
    }

	@Override
	@Transactional
	public Situation toggleMotorcycleStatus(Integer motorcycleId) {
		Motorcycle motorcycle = findMotorcycleById(motorcycleId);
		if (motorcycle.getMotorcycleStatus().equals(Situation.ACTIVE)) {
			motorcycle.setMotorcycleStatus(Situation.INACTIVE);
		} else if (motorcycle.getMotorcycleStatus().equals(Situation.INACTIVE)) {
			motorcycle.setMotorcycleStatus(Situation.ACTIVE);
		}
		this.motorcycleRepository.save(motorcycle);
		return motorcycle.getMotorcycleStatus();
	}

	@Override
	@Transactional
	public Motorcycle updateMotorcycle(MotorcycleDTO motorcycleDTO) {
		Motorcycle motorcycle = this.mapper.map(motorcycleDTO, Motorcycle.class);

		LocalDateTime dateTime = findMotorcycleById(motorcycleDTO.getId()).getCreatedAt();
		motorcycle.setCreatedAt(dateTime);

		this.validateMotorcyclePlateToUpdate(motorcycleDTO);
		motorcycleDTO.setPlate(motorcycleDTO.getPlate().toUpperCase());

        CustomerDTO customerDTO = motorcycleDTO.getCustomer();
        if (customerDTO != null) {
            Customer customer = this.customerService.findCustomerByCpf(customerDTO.getCpf());
            motorcycle.setCustomer(customer);
        }

		return this.motorcycleRepository.save(motorcycle);
	}

    @Override
    @Transactional
    public void deleteMotorcycleById(Integer motorcycleId) {
        this.existsMotorcycleById(motorcycleId);
        this.motorcycleRepository.deleteById(motorcycleId);
    }

    @Override
    @Transactional
    public void deleteMotorcycleByPlate(String plate) {
        String plateWithCapitalLetters = plate.toUpperCase();
        this.existsMotorcycleByPlate(plateWithCapitalLetters);
        this.motorcycleRepository.deleteByPlate(plateWithCapitalLetters);
    }

    @Override
    public void validateMotorcyclePlateToSave(String plate) {
        if (this.motorcycleRepository.existsMotorcycleByPlate(plate)) {
            throw new BusinessRuleException("Erro ao tentar salvar, Placa já cadastrada.");
        }
    }

    @Override
    public void validateMotorcyclePlateToUpdate(MotorcycleDTO motorcycleDTO) {
        this.filterMotorcyclesByDifferentId(motorcycleDTO.getId()).forEach(filteredMotorcycle -> {
            if (motorcycleDTO.getPlate().equals(filteredMotorcycle.getPlate())) {
                throw new BusinessRuleException("Erro ao tentar atualizar Moto, Placa já cadastrada.");
            }
        });
    }

    private List<Motorcycle> filterMotorcyclesByDifferentId(Integer idMoto) {
        return this.motorcycleRepository.findByIdNot(idMoto);
    }

    @Override
    public void existsMotorcycleById(Integer motorcycleId) {
        if (!this.motorcycleRepository.existsById(motorcycleId)) {
            throw new ObjectNotFoundException(MOTORCYCLE_NOT_FOUND_ID_MSG);
        }
    }

    @Override
    public void existsMotorcycleByPlate(String plate) {
        if (!this.motorcycleRepository.existsMotorcycleByPlate(plate)) {
            throw new ObjectNotFoundException(MOTORCYCLE_NOT_FOUND_PLATE_MSG);
        }
    }

    @Override
    public void existsMotorcycleByCustomerCpf(String customerCpf) {
        if (!this.motorcycleRepository.existsMotorcycleByCustomerCpf(customerCpf)) {
            throw new BusinessRuleException("O cliente não possui nenhuma moto cadastrada.");
        }
    }
}
