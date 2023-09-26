package com.example.jbmotos.model.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jbmotos.model.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, String> {

    Optional<Cliente> findClienteByCpf(String cpf);

    boolean existsClienteByCpf(String cpf);

    boolean existsClienteByEmail(String email);

    void deleteClienteByCpf(String cpf);

    boolean existsClienteByEnderecoId(Integer idEndereco);

    List<Cliente> findByCpfNot(String cpf);
}
