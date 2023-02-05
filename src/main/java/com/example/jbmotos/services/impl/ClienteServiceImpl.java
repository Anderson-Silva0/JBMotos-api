package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.ClienteDTO;
import com.example.jbmotos.model.entity.Cliente;
import com.example.jbmotos.model.repositories.ClienteRepository;
import com.example.jbmotos.services.ClienteService;
import com.example.jbmotos.services.EnderecoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoService enderecoService;
    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Cliente salvarCliente(ClienteDTO clienteDTO) {
        validaEmailCpfEEnderecoParaSalvarCliente(clienteDTO);
        Cliente cliente = mapper.map(clienteDTO, Cliente.class);
        cliente.setEndereco(enderecoService.buscarEnderecoPorId(clienteDTO.getEndereco()).get());
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarTodosClientes() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarClientePorCPF(String cpf) {
        if (!clienteRepository.existsClienteByCpf(cpf)) {
            throw new ObjetoNaoEncontradoException("Cliente não encontrado para o CPF informado.");
        }
        return clienteRepository.findClienteByCpf(cpf);
    }

    @Override
    @Transactional
    public Cliente atualizarCliente(ClienteDTO clienteDTO) {
        validaEmailCpfEEnderecoParaAtualizarCliente(clienteDTO);
        Cliente cliente = mapper.map(clienteDTO, Cliente.class);
        cliente.setEndereco(enderecoService.buscarEnderecoPorId(clienteDTO.getEndereco()).get());
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void deletarCliente(String cpf) {
        if (!clienteRepository.existsClienteByCpf(cpf)) {
            throw new ObjetoNaoEncontradoException("Cliente não encontrado para o CPF informado.");
        }
        clienteRepository.deleteClienteByCpf(cpf);
    }

    @Override
    public void validaEmailCpfEEnderecoParaAtualizarCliente(ClienteDTO clienteDTO) {
        Cliente cliente = buscarClientePorCPF(clienteDTO.getCpf()).get();
        if (!clienteRepository.existsClienteByCpf(clienteDTO.getCpf())) {
            throw new ObjetoNaoEncontradoException("Cliente não encontrado para o CPF informado.");
        }
        if (!enderecoService.existeEnderecoPorId(clienteDTO.getEndereco())) {
            throw new ObjetoNaoEncontradoException("Erro ao tentar atualizar Cliente, o Endereço não existe.");
        }
        if (cliente.getEndereco().getId() != clienteDTO.getEndereco()) {
            throw new RegraDeNegocioException("Erro ao tentar atualizar Cliente, " +
                    "o Endereço pertence à outro Cliente.");
        }
        buscarTodosClientes().stream().filter(c -> cliente.getCpf() != c.getCpf() )
                    .forEach(clienteFiltrado -> {
                    if (clienteDTO.getEmail().equals( clienteFiltrado.getEmail() )) {
                        throw new RegraDeNegocioException("Erro ao tentar atualizar Cliente, Email já cadastrado.");
                    }
                });
    }
    @Override
    public void validaEmailCpfEEnderecoParaSalvarCliente(ClienteDTO clienteDTO) {
        if (clienteRepository.existsClienteByCpf(clienteDTO.getCpf())) {
            throw new RegraDeNegocioException("Erro ao tentar salvar Cliente, CPF já cadastrado.");
        }
        if(clienteRepository.existsClienteByEmail(clienteDTO.getEmail())){
            throw new RegraDeNegocioException("Erro ao tentar salvar Cliente, Email já cadastrado.");
        }
        buscarTodosClientes().stream().forEach(c -> {
            if (clienteDTO.getEndereco() == c.getEndereco().getId()) {
                throw new RegraDeNegocioException("Erro ao tentar salvar Cliente, o Endereço já pertence a um Cliente.");
            }
        });
    }
}
