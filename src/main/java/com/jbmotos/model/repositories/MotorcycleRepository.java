package com.jbmotos.model.repositories;

import java.util.List;
import java.util.Optional;

import com.jbmotos.model.entity.Motorcycle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotorcycleRepository extends JpaRepository<Motorcycle, Integer> {

    List<Motorcycle> findMotorcyclesByCustomerCpf(String customerCpf);

    Optional<Motorcycle> findMotorcycleByPlate(String plate);

    boolean existsMotorcycleByPlate(String plate);

    boolean existsMotorcycleByCustomerCpf(String cpf);

    void deleteByPlate(String plate);

    List<Motorcycle> findByIdNot(Integer motorcycleId);
}
