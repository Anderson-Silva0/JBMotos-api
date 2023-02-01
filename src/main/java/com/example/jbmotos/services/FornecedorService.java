package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.FornecedorDTO;
import com.example.jbmotos.model.entity.Fornecedor;

import java.util.List;
import java.util.Optional;

public interface FornecedorService {

    Fornecedor salvarFornecedor(FornecedorDTO fornecedorDTO);

    List<Fornecedor> buscarTodosFornecedores();

    Optional<Fornecedor> buscarFornecedorPorCNPJ(String cnpj);

    Fornecedor atualizarFornecedor(FornecedorDTO fornecedorDTO);

    void deletarFornecedor(String cnpj);
}
