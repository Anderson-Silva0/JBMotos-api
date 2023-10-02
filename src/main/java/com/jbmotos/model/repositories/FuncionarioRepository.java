package com.jbmotos.model.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbmotos.model.entity.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, String> {
	
    Optional<Funcionario> findFuncionarioByCpf(String cpf);

    boolean existsFuncionarioByCpf(String cpf);

    void deleteFuncionarioByCpf(String cpf);

    boolean existsFuncionarioByEnderecoId(Integer idEndereco);

    List<Funcionario> findByCpfNot(String cpf);
}
