package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.ClienteDTO;
import com.example.jbmotos.model.entity.Cliente;
import com.example.jbmotos.model.repositories.ClienteRepository;
import com.example.jbmotos.services.ClienteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Cliente salvarCliente(ClienteDTO clienteDTO) {
        //lembrar que o email do cliente é único. unique = true.
        return clienteRepository.save(mapper.map(clienteDTO, Cliente.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarTodosClientes() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarClientePorCPF(String cpf) {
        return clienteRepository.findClienteByCpf(cpf);
    }

    @Override
    @Transactional
    public Cliente atualizarCliente(ClienteDTO clienteDTO) {
        Objects.requireNonNull(clienteDTO.getCpf(), "Erro ao tentar atualizar a Cliente. Informe um CPF.");
        return clienteRepository.save(mapper.map(clienteDTO, Cliente.class));
    }

    @Override
    @Transactional
    public void deletarCliente(String cpf) {
        clienteRepository.deleteClienteByCpf(cpf);
    }
}
