package com.jbmotos.services;

import java.util.List;

import com.jbmotos.api.dto.MotorcycleDTO;
import com.jbmotos.model.entity.Motorcycle;
import com.jbmotos.model.enums.Situation;

public interface MotorcycleService {

    Motorcycle saveMotorcycle(MotorcycleDTO motorcycleDTO);

    List<Motorcycle> findAllMotorcycles();

    List<Motorcycle> findMotorcycleByCustomerCpf(String customerCpf);

    Motorcycle findMotorcycleById(Integer motorcycleId);

    Motorcycle findMotorcycleByPlate(String plate);

    List<Motorcycle> filterMotorcycle(MotorcycleDTO motorcycleDTO);

    Situation toggleMotorcycleStatus(Integer motorcycleId);

    Motorcycle updateMotorcycle(MotorcycleDTO motorcycleDTO);

    void deleteMotorcycleById(Integer motorcycleId);

    void deleteMotorcycleByPlate(String plate);

    void validateMotorcyclePlateToSave(String plate);

    void validateMotorcyclePlateToUpdate(MotorcycleDTO motorcycleDTO);

    void existsMotorcycleById(Integer motorcycleId);

    void existsMotorcycleByPlate(String plate);

    void existsMotorcycleByCustomerCpf(String customerCpf);
}
