package com.example.jbmotos.model.repositories;

import com.example.jbmotos.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {
    Optional<Cliente> findClienteByCpf(String cpf);

    boolean existsClienteByCpf(String cpf);

    boolean existsClienteByEmail(String email);

    void deleteClienteByCpf(String cpf);

    boolean existsClienteByEnderecoId(Integer idEndereco);
}
