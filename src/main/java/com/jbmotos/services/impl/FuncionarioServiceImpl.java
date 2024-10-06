package com.jbmotos.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
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

	private final String FUNCIONARIO_NAO_ENCONTRADO = "Funcionário não encrontrado para o CPF informado.";

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
		Endereco enderecoSalvo = enderecoService.salvarEndereco(funcionarioDTO.getEndereco());
		
		validarCpfFuncionarioParaSalvar(funcionarioDTO.getCpf());
		Funcionario funcionario = mapper.map(funcionarioDTO, Funcionario.class);
		funcionario.setStatusFuncionario(Situacao.ATIVO);

		funcionario.setEndereco(enderecoSalvo);

		return funcionarioRepository.save(funcionario);
	}

    @Override
    @Transactional(readOnly = true)
    public List<Funcionario> buscarTodosFuncionarios() {
        return funcionarioRepository.findAll();
    }

	@Override
	@Transactional(readOnly = true)
	public Funcionario buscarFuncionarioPorCPF(String cpf) {
		return funcionarioRepository.findFuncionarioByCpf(cpf)
				.orElseThrow(() -> new ObjetoNaoEncontradoException(FUNCIONARIO_NAO_ENCONTRADO));
	}

    @Override
    @Transactional(readOnly = true)
    public List<Funcionario> filtrarFuncionario(FuncionarioDTO funcionarioDTO) {
        Example<Funcionario> example = Example.of(mapper.map(funcionarioDTO, Funcionario.class),
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

		Sort sort = Sort.by(Sort.Direction.DESC, "dataHoraCadastro");

        return funcionarioRepository.findAll(example, sort);
    }

	@Override
	@Transactional
	public Situacao alternarStatusFuncionario(String cpf) {
		Funcionario funcionario = buscarFuncionarioPorCPF(cpf);
		if (funcionario.getStatusFuncionario().equals(Situacao.ATIVO)) {
			funcionario.setStatusFuncionario(Situacao.INATIVO);
		} else if (funcionario.getStatusFuncionario().equals(Situacao.INATIVO)) {
			funcionario.setStatusFuncionario(Situacao.ATIVO);
		}
		funcionarioRepository.save(funcionario);
		return funcionario.getStatusFuncionario();
	}

	@Override
	@Transactional
	public Funcionario atualizarFuncionario(FuncionarioDTO funcionarioDTO) {
		Funcionario funcionario = mapper.map(funcionarioDTO, Funcionario.class);

		LocalDateTime dateTime = buscarFuncionarioPorCPF(funcionarioDTO.getCpf()).getDataHoraCadastro();
		funcionario.setDataHoraCadastro(dateTime);

		Endereco endereco = mapper.map(funcionarioDTO.getEndereco(), Endereco.class);
		funcionario.setEndereco(endereco);

		return funcionarioRepository.save(funcionario);
	}

	@Override
	@Transactional
	public void deletarFuncionario(String cpf) {
		checarCpfFuncionarioExistente(cpf);
		funcionarioRepository.deleteFuncionarioByCpf(cpf);
	}

	@Override
	public void checarCpfFuncionarioExistente(String cpf) {
		if (!funcionarioRepository.existsFuncionarioByCpf(cpf)) {
			throw new ObjetoNaoEncontradoException(FUNCIONARIO_NAO_ENCONTRADO);
		}
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
    public boolean existeFuncionarioPorIdEndereco(Integer idEndereco) {
        return funcionarioRepository.existsFuncionarioByEnderecoId(idEndereco);
    }
}
