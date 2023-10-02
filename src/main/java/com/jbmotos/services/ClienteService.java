package com.jbmotos.services;

import java.util.List;
import java.util.Optional;

import com.jbmotos.api.dto.ClienteDTO;
import com.jbmotos.model.entity.Cliente;
import com.jbmotos.model.enums.Situacao;

public interface ClienteService {

    Cliente salvarCliente(ClienteDTO clienteDTO);

    List<Cliente> buscarTodosClientes();

    Optional<Cliente> buscarClientePorCPF(String cpf);

    List<Cliente> filtrarCliente(ClienteDTO clienteDTO);

    Situacao alternarStatusCliente(String cpf);

    Cliente atualizarCliente(ClienteDTO clienteDTO);

    void deletarCliente(String cpf);

    void validarEmailParaSalvar(String email);

    void validarCpfClienteParaSalvar(String cpf);

    void validarEmailParaAtualizar(ClienteDTO clienteDTO);

    void checarCpfClienteExistente(String cpf);

    boolean existeClientePorIdEndereco(Integer idEndereco);
}
