package com.jbmotos.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.FuncionarioDTO;
import com.jbmotos.model.entity.Endereco;
import com.jbmotos.model.entity.Funcionario;
import com.jbmotos.model.enums.Situacao;
import com.jbmotos.model.repositories.FuncionarioRepository;
import com.jbmotos.services.EnderecoService;
import com.jbmotos.services.FuncionarioService;
import com.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.jbmotos.services.exception.RegraDeNegocioException;

@Service
public class FuncionarioServiceImpl implements FuncionarioService {

    private final String ERRO_SALVAR_FUNCIONARIO = "Erro ao tentar salvar Funcionário";

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private ModelMapper mapper;

	@Override
	@Transactional
	public Funcionario salvarFuncionario(FuncionarioDTO funcionarioDTO) {
		validarCpfFuncionarioParaSalvar(funcionarioDTO.getCpf());
		Funcionario funcionario = mapper.map(funcionarioDTO, Funcionario.class);
		funcionario.setStatusFuncionario(Situacao.ATIVO);
		funcionario.setDataHoraCadastro(LocalDateTime.now());

		Optional<Endereco> enderecoOptional = enderecoService.buscarEnderecoPorId(funcionarioDTO.getEndereco());
		if (enderecoOptional.isPresent()) {
			funcionario.setEndereco(enderecoOptional.get());
		}
		return funcionarioRepository.save(funcionario);
	}

    @Override
    @Transactional(readOnly = true)
    public List<Funcionario> buscarTodosFuncionarios() {
        return funcionarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Funcionario> buscarFuncionarioPorCPF(String cpf) {
        checarCpfFuncionarioExistente(cpf);
        return funcionarioRepository.findFuncionarioByCpf(cpf);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Funcionario> filtrarFuncionario(FuncionarioDTO funcionarioDTO) {
        Example<Funcionario> example = Example.of(mapper.map(funcionarioDTO, Funcionario.class),
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return funcionarioRepository.findAll(example);
    }

	@Override
	@Transactional
	public Situacao alternarStatusFuncionario(String cpf) {
		Optional<Funcionario> funcionarioOptional = buscarFuncionarioPorCPF(cpf);
		if (funcionarioOptional.isPresent()) {
			if (funcionarioOptional.get().getStatusFuncionario().equals(Situacao.ATIVO)) {
				funcionarioOptional.get().setStatusFuncionario(Situacao.INATIVO);
			} else if (funcionarioOptional.get().getStatusFuncionario().equals(Situacao.INATIVO)) {
				funcionarioOptional.get().setStatusFuncionario(Situacao.ATIVO);
			}
			funcionarioRepository.save(funcionarioOptional.get());
			return funcionarioOptional.get().getStatusFuncionario();
		}
		return null;
	}

	@Override
	@Transactional
	public Funcionario atualizarFuncionario(FuncionarioDTO funcionarioDTO) {
		Optional<Funcionario> funcionarioOptional = buscarFuncionarioPorCPF(funcionarioDTO.getCpf());
		if (funcionarioOptional.isPresent()) {
			LocalDateTime dateTime = funcionarioOptional.get().getDataHoraCadastro();
			Funcionario funcionario = mapper.map(funcionarioDTO, Funcionario.class);
			funcionario.setDataHoraCadastro(dateTime);

			Optional<Endereco> enderecoOptional = enderecoService.buscarEnderecoPorId(funcionarioDTO.getEndereco());
			if (enderecoOptional.isPresent()) {
				funcionario.setEndereco(enderecoOptional.get());
			}
			return funcionarioRepository.save(funcionario);
		}
		return null;
	}

    @Override
    @Transactional
    public void deletarFuncionario(String cpf) {
        checarCpfFuncionarioExistente(cpf);
        funcionarioRepository.deleteFuncionarioByCpf(cpf);
    }

    @Override
    public void validarCpfFuncionarioParaSalvar(String cpf) {
        if (funcionarioRepository.existsFuncionarioByCpf(cpf)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_FUNCIONARIO + ", CPF já cadastrado.");
        }
    }

    @Override
    public List<Funcionario> filtrarFuncionariosPorCpfDiferente(FuncionarioDTO funcionarioDTO) {
        return funcionarioRepository.findByCpfNot(funcionarioDTO.getCpf());
    }

    @Override
    public void checarCpfFuncionarioExistente(String cpf) {
        if (!funcionarioRepository.existsFuncionarioByCpf(cpf)) {
            throw new ObjetoNaoEncontradoException("Funcionário não encrontrado para o CPF informado.");
        }
    }

    @Override
    public boolean existeFuncionarioPorIdEndereco(Integer idEndereco) {
        return funcionarioRepository.existsFuncionarioByEnderecoId(idEndereco);
    }
}
