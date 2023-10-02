package com.jbmotos.services;

import java.util.List;
import java.util.Optional;

import com.jbmotos.api.dto.FornecedorDTO;
import com.jbmotos.model.entity.Fornecedor;
import com.jbmotos.model.enums.Situacao;

public interface FornecedorService {

    Fornecedor salvarFornecedor(FornecedorDTO fornecedorDTO);

    List<Fornecedor> buscarTodosFornecedores();

    Optional<Fornecedor> buscarFornecedorPorCNPJ(String cnpj);

    List<Fornecedor> filtrarFornecedor(FornecedorDTO fornecedorDTO);

    Situacao alternarStatusFornecedor(String cnpj);

    Fornecedor atualizarFornecedor(FornecedorDTO fornecedorDTO);

    void deletarFornecedor(String cnpj);

    void validarCnpjFornecedorParaSalvar(String cnpj);

    List<Fornecedor> filtrarFornecedoresPorCnpjDiferente(FornecedorDTO fornecedorDTO);

    void checarCnpjFornecedorExistente(String cnpj);

    boolean existeFornecedorPorIdEndereco(Integer idEndereco);
}
