package com.jbmotos.services;

import java.util.List;
import java.util.Optional;

import com.jbmotos.api.dto.FuncionarioDTO;
import com.jbmotos.model.entity.Funcionario;
import com.jbmotos.model.enums.Situacao;

public interface FuncionarioService {

    Funcionario salvarFuncionario(FuncionarioDTO funcionarioDTO);

    List<Funcionario> buscarTodosFuncionarios();

    Optional<Funcionario> buscarFuncionarioPorCPF(String cpf);

    List<Funcionario> filtrarFuncionario(FuncionarioDTO funcionarioDTO);

    Situacao alternarStatusFuncionario(String cpf);

    Funcionario atualizarFuncionario(FuncionarioDTO funcionarioDTO);

    void deletarFuncionario(String cpf);

    void validarCpfFuncionarioParaSalvar(String cpf);

    List<Funcionario> filtrarFuncionariosPorCpfDiferente(FuncionarioDTO funcionarioDTO);

    void checarCpfFuncionarioExistente(String cpf);

    boolean existeFuncionarioPorIdEndereco(Integer idEndereco);
}
