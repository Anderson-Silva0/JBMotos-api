package com.example.jbmotos.model.repositories;

import com.example.jbmotos.model.entity.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {

    Optional<Fornecedor> findFornecedorByCnpj(String cnpj);

    void deleteFornecedorByCnpj(String cnpj);
}
