package com.example.jbmotos.model.repositories;

import com.example.jbmotos.model.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, String> {
    Optional<Funcionario> findFuncionarioByCpf(String cpf);

    void deleteFuncionarioByCpf(String cpf);
}
