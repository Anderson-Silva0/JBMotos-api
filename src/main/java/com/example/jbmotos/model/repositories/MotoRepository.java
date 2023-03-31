package com.example.jbmotos.model.repositories;

import com.example.jbmotos.model.entity.Moto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MotoRepository extends JpaRepository<Moto, Integer> {

    List<Moto> findMotosByClienteCpf(String cpfCliente);

    Optional<Moto> findMotoByPlaca(String placa);

    boolean existsMotoByPlaca(String placa);

    boolean existsMotoByClienteCpf(String cpf);

    void deleteByPlaca(String placa);

    List<Moto> findByIdNot(Integer idMoto);
}
