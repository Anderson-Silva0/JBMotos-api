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

    void validarEnderecoParaSalvar(Integer idEndereco);

    void validarCnpjFornecedorParaSalvar(String cnpj);

    void validarEnderecoParaAtualizar(FornecedorDTO fornecedorDTO);

    List<Fornecedor> filtrarFornecedoresPorCnpjDiferente(FornecedorDTO fornecedorDTO);

    void checarCnpjFornecedorExistente(String cnpj);

    boolean existeFornecedorPorIdEndereco(Integer idEndereco);
}
