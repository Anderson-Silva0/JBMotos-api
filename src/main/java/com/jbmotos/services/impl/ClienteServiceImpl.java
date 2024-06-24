package com.jbmotos.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.ClienteDTO;
import com.jbmotos.model.entity.Cliente;
import com.jbmotos.model.entity.Endereco;
import com.jbmotos.model.enums.Situacao;
import com.jbmotos.model.repositories.ClienteRepository;
import com.jbmotos.services.ClienteService;
import com.jbmotos.services.EnderecoService;
import com.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.jbmotos.services.exception.RegraDeNegocioException;

@Service
public class ClienteServiceImpl implements ClienteService {

	private final String CLIENTE_NAO_ENCONTRADO = "Cliente não encrontrado para o CPF informado.";

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
		Endereco enderecoSalvo = enderecoService.salvarEndereco(clienteDTO.getEndereco());
		
		validarCpfClienteParaSalvar(clienteDTO.getCpf());
		validarEmailParaSalvar(clienteDTO.getEmail());
		Cliente cliente = mapper.map(clienteDTO, Cliente.class);
		cliente.setStatusCliente(Situacao.ATIVO);

		cliente.setEndereco(enderecoSalvo);

		return clienteRepository.save(cliente);
	}

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarTodosClientes() {
        return clienteRepository.findAll();
    }

	@Override
	@Transactional(readOnly = true)
	public Cliente buscarClientePorCPF(String cpf) {
		return clienteRepository.findClienteByCpf(cpf)
				.orElseThrow(() -> new ObjetoNaoEncontradoException(CLIENTE_NAO_ENCONTRADO));
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
		Cliente cliente = buscarClientePorCPF(cpf);
		if (cliente.getStatusCliente().equals(Situacao.ATIVO)) {
			cliente.setStatusCliente(Situacao.INATIVO);
		} else if (cliente.getStatusCliente().equals(Situacao.INATIVO)) {
			cliente.setStatusCliente(Situacao.ATIVO);
		}
		clienteRepository.save(cliente);
		return cliente.getStatusCliente();
	}

	@Override
	@Transactional
	public Cliente atualizarCliente(ClienteDTO clienteDTO) {
		Cliente cliente = mapper.map(clienteDTO, Cliente.class);

		LocalDateTime dateTime = buscarClientePorCPF(clienteDTO.getCpf()).getDataHoraCadastro();
		cliente.setDataHoraCadastro(dateTime);

		validarEmailParaAtualizar(clienteDTO);

		Endereco endereco = mapper.map(clienteDTO.getEndereco(), Endereco.class);
		cliente.setEndereco(endereco);

		return clienteRepository.save(cliente);
	}

	@Override
	@Transactional
	public void deletarCliente(String cpf) {
		checarCpfClienteExistente(cpf);
		clienteRepository.deleteClienteByCpf(cpf);
	}

	@Override
	public void checarCpfClienteExistente(String cpf) {
		if (!clienteRepository.existsClienteByCpf(cpf)) {
			throw new ObjetoNaoEncontradoException(CLIENTE_NAO_ENCONTRADO);
		}
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
    public boolean existeClientePorIdEndereco(Integer idEndereco) {
        return clienteRepository.existsClienteByEnderecoId(idEndereco);
    }
}
