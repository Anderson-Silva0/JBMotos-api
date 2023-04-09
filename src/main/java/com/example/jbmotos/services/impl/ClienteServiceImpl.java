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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final String ERRO_SALVAR_CLIENTE = "Erro ao tentar salvar Cliente";

    private final String ERRO_ATUALIZAR_CLIENTE = "Erro ao tentar atualizar Cliente";

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Cliente salvarCliente(ClienteDTO clienteDTO) {
        validarCpfClienteParaSalvar(clienteDTO.getCpf());
        validarEmailParaSalvar(clienteDTO.getEmail());
        Cliente cliente = mapper.map(clienteDTO, Cliente.class);
        cliente.setDataHoraCadastro(LocalDateTime.now());
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
        LocalDateTime dateTime = buscarClientePorCPF(clienteDTO.getCpf()).get().getDataHoraCadastro();
        validarEmailParaAtualizar(clienteDTO);
        Cliente cliente = mapper.map(clienteDTO, Cliente.class);
        cliente.setDataHoraCadastro(dateTime);
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

    private List<Cliente> filtrarClientesPorCpfDiferente(ClienteDTO clienteDTO) {
        return clienteRepository.findByCpfNot(clienteDTO.getCpf());
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
