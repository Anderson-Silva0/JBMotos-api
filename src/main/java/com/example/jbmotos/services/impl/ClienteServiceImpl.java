package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.ClienteDTO;
import com.example.jbmotos.model.entity.Cliente;
import com.example.jbmotos.model.repositories.ClienteRepository;
import com.example.jbmotos.services.ClienteService;
import com.example.jbmotos.services.EnderecoService;
import com.example.jbmotos.services.FornecedorService;
import com.example.jbmotos.services.FuncionarioService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final String ERRO_SALVAR_CLIENTE = "Erro ao tentar salvar Cliente";
    private final String ERRO_ATUALIZAR_CLIENTE = "Erro ao tentar atualizar Cliente";

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private FornecedorService fornecedorService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Cliente salvarCliente(ClienteDTO clienteDTO) {
        validarCpfClienteParaSalvar(clienteDTO.getCpf());
        validarEmailParaSalvar(clienteDTO.getEmail());
        validarEnderecoParaSalvar(clienteDTO.getEndereco());

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
        checarCpfClienteExistente(cpf);
        return clienteRepository.findClienteByCpf(cpf);
    }

    @Override
    @Transactional
    public Cliente atualizarCliente(ClienteDTO clienteDTO) {
        checarCpfClienteExistente(clienteDTO.getCpf());
        validarEmailParaAtualizar(clienteDTO);
        validarEnderecoParaAtualizar(clienteDTO);

        Cliente cliente = mapper.map(clienteDTO, Cliente.class);
        cliente.setEndereco(enderecoService.buscarEnderecoPorId(clienteDTO.getEndereco()).get());
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void deletarCliente(String cpf) {
        checarCpfClienteExistente(cpf);
        clienteRepository.deleteClienteByCpf(cpf);
    }

    @Override
    public void validarEmailParaSalvar(String email) {
        if (clienteRepository.existsClienteByEmail(email)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_CLIENTE+", Email já cadastrado.");
        }
    }

    @Override
    public void validarEnderecoParaSalvar(Integer idEndereco) {
        if (existeClientePorIdEndereco(idEndereco)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_CLIENTE+", o Endereço já pertence a um Cliente.");
        }
        if (funcionarioService.existeFuncionarioPorIdEndereco(idEndereco)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_CLIENTE+", o Endereço já pertence a um Funcionário.");
        }
        if (fornecedorService.existeFornecedorPorIdEndereco(idEndereco)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_CLIENTE+", o Endereço já pertence a um Fornecedor.");
        }
    }

    @Override
    public void validarCpfClienteParaSalvar(String cpf) {
        if (clienteRepository.existsClienteByCpf(cpf)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_CLIENTE+", CPF já cadastrado.");
        }
    }

    @Override
    public void validarEmailParaAtualizar(ClienteDTO clienteDTO) {
        filtrarClientesPorCpfDiferente(clienteDTO).stream().forEach(clienteFiltrado -> {
            if (clienteDTO.getEmail().equals( clienteFiltrado.getEmail())) {
                throw new RegraDeNegocioException(ERRO_ATUALIZAR_CLIENTE+", Email já cadastrado.");
            }
        });
    }

    @Override
    public void validarEnderecoParaAtualizar(ClienteDTO clienteDTO) {
        filtrarClientesPorCpfDiferente(clienteDTO).stream().forEach(clienteFiltrado -> {
            if (clienteDTO.getEndereco() == clienteFiltrado.getEndereco().getId()) {
                throw new RegraDeNegocioException(ERRO_ATUALIZAR_CLIENTE+", o Endereço já pertence a um Cliente.");
            }
            if (funcionarioService.existeFuncionarioPorIdEndereco(clienteDTO.getEndereco())) {
                throw new RegraDeNegocioException(ERRO_ATUALIZAR_CLIENTE+", o Endereço já pertence a um Funcionário.");
            }
            if (fornecedorService.existeFornecedorPorIdEndereco(clienteDTO.getEndereco())) {
                throw new RegraDeNegocioException(ERRO_ATUALIZAR_CLIENTE+", o Endereço já pertence a um Fornecedor.");
            }
        });
    }

    private List<Cliente> filtrarClientesPorCpfDiferente(ClienteDTO clienteDTO) {
        return buscarTodosClientes().stream()
                .filter(cliente -> (!clienteDTO.getCpf().equals(cliente.getCpf())))
                .collect(Collectors.toList());
    }

    @Override
    public void checarCpfClienteExistente(String cpf){
        if (!clienteRepository.existsClienteByCpf(cpf)) {
            throw new ObjetoNaoEncontradoException("Cliente não encrontrado para o CPF informado.");
        }
    }

    @Override
    public boolean existeClientePorIdEndereco(Integer idEndereco) {
        return clienteRepository.existsClienteByEnderecoId(idEndereco);
    }
}
