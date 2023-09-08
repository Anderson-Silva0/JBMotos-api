package com.example.jbmotos.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jbmotos.api.dto.ClienteDTO;
import com.example.jbmotos.model.entity.Cliente;
import com.example.jbmotos.model.entity.Endereco;
import com.example.jbmotos.model.enums.Situacao;
import com.example.jbmotos.model.repositories.ClienteRepository;
import com.example.jbmotos.services.ClienteService;
import com.example.jbmotos.services.EnderecoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;

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
        cliente.setStatusCliente(Situacao.ATIVO);

        Optional<Endereco> enderecoOptional = enderecoService.buscarEnderecoPorId(clienteDTO.getEndereco());
        if (enderecoOptional.isPresent()) {
            cliente.setEndereco(enderecoOptional.get());
		}

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
    @Transactional(readOnly = true)
    public List<Cliente> filtrarCliente(ClienteDTO clienteDTO) {
        Example<Cliente> example = Example.of(mapper.map(clienteDTO, Cliente.class),
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return clienteRepository.findAll(example);
    }

	@Override
	@Transactional
	public Situacao alternarStatusCliente(String cpf) {
		Optional<Cliente> clienteOptional = buscarClientePorCPF(cpf);
		if (clienteOptional.isPresent()) {
			if (clienteOptional.get().getStatusCliente().equals(Situacao.ATIVO)) {
				clienteOptional.get().setStatusCliente(Situacao.INATIVO);
			} else if (clienteOptional.get().getStatusCliente().equals(Situacao.INATIVO)) {
				clienteOptional.get().setStatusCliente(Situacao.ATIVO);
			}
			clienteRepository.save(clienteOptional.get());
			return clienteOptional.get().getStatusCliente();
		}
		return null;
	}

    @Override
    @Transactional
	public Cliente atualizarCliente(ClienteDTO clienteDTO) {
		Cliente cliente = mapper.map(clienteDTO, Cliente.class);

		Optional<Cliente> clienteOptional = buscarClientePorCPF(clienteDTO.getCpf());
		if (clienteOptional.isPresent()) {
			LocalDateTime dateTime = clienteOptional.get().getDataHoraCadastro();
			cliente.setDataHoraCadastro(dateTime);
		}

		validarEmailParaAtualizar(clienteDTO);

		Optional<Endereco> enderecoOptional = enderecoService.buscarEnderecoPorId(clienteDTO.getEndereco());
		if (enderecoOptional.isPresent()) {
			cliente.setEndereco(enderecoOptional.get());
		}

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
            if (clienteDTO.getEmail().equals(clienteFiltrado.getEmail())) {
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
