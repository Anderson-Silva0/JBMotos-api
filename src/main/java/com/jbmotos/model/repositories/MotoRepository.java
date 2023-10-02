package com.jbmotos.model.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbmotos.model.entity.Moto;

public interface MotoRepository extends JpaRepository<Moto, Integer> {

    List<Moto> findMotosByClienteCpf(String cpfCliente);

    Optional<Moto> findMotoByPlaca(String placa);

    boolean existsMotoByPlaca(String placa);

    boolean existsMotoByClienteCpf(String cpf);

    void deleteByPlaca(String placa);

    List<Moto> findByIdNot(Integer idMoto);
}
