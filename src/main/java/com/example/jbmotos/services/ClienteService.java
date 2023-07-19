package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.ClienteDTO;
import com.example.jbmotos.model.entity.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {

    Cliente salvarCliente(ClienteDTO clienteDTO);

    List<Cliente> buscarTodosClientes();

    Optional<Cliente> buscarClientePorCPF(String cpf);

    List<Cliente> filtrarCliente(ClienteDTO clienteDTO);

    Cliente atualizarCliente(ClienteDTO clienteDTO);

    void deletarCliente(String cpf);

    void validarEmailParaSalvar(String email);

    void validarCpfClienteParaSalvar(String cpf);

    void validarEmailParaAtualizar(ClienteDTO clienteDTO);

    void checarCpfClienteExistente(String cpf);

    boolean existeClientePorIdEndereco(Integer idEndereco);
}
