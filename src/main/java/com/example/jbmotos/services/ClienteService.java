package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.ClienteDTO;
import com.example.jbmotos.model.entity.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {

    Cliente salvarCliente(ClienteDTO clienteDTO);

    List<Cliente> buscarTodosClientes();

    Optional<Cliente> buscarClientePorCPF(String cpf);

    Cliente atualizarCliente(ClienteDTO clienteDTO);

    void deletarCliente(String cpf);

    void validaEmailCpfEEnderecoParaAtualizarCliente(ClienteDTO clienteDTO);

    void validaEmailCpfEEnderecoParaSalvarCliente(ClienteDTO clienteDTO);

    void existeClientePorCpf(String cpf, String tipoOperacao);
}
