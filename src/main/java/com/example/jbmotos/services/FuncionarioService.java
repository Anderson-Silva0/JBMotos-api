package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.FuncionarioDTO;
import com.example.jbmotos.model.entity.Funcionario;

import java.util.List;
import java.util.Optional;

public interface FuncionarioService {

    Funcionario salvarFuncionario(FuncionarioDTO funcionarioDTO);

    List<Funcionario> buscarTodosFuncionarios();

    Optional<Funcionario> buscarFuncionarioPorCPF(String cpf);

    Funcionario atualizarFuncionario(FuncionarioDTO funcionarioDTO);

    void deletarFuncionario(String cpf);

    void validarEnderecoParaSalvar(Integer idEndereco);

    void validarCpfFuncionarioParaSalvar(String cpf);

    void validarEnderecoParaAtualizar(FuncionarioDTO funcionarioDTO);

    List<Funcionario> filtrarFuncionariosPorCpfDiferente(FuncionarioDTO funcionarioDTO);

    void checarCpfFuncionarioExistente(String cpf);

    boolean existeFuncionarioPorIdEndereco(Integer idEndereco);
}
