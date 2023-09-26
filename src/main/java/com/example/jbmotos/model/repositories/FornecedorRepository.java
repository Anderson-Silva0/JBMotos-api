package com.example.jbmotos.model.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jbmotos.model.entity.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {

    Optional<Fornecedor> findFornecedorByCnpj(String cnpj);

    boolean existsFornecedorByCnpj(String cnpj);

    void deleteFornecedorByCnpj(String cnpj);

    boolean existsFornecedorByEnderecoId(Integer idEndereco);

    List<Fornecedor> findByCnpjNot(String cnpj);
}
