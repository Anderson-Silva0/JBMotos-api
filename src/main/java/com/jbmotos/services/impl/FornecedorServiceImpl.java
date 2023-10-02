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

import com.jbmotos.api.dto.FornecedorDTO;
import com.jbmotos.model.entity.Endereco;
import com.jbmotos.model.entity.Fornecedor;
import com.jbmotos.model.enums.Situacao;
import com.jbmotos.model.repositories.FornecedorRepository;
import com.jbmotos.services.EnderecoService;
import com.jbmotos.services.FornecedorService;
import com.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.jbmotos.services.exception.RegraDeNegocioException;

@Service
public class FornecedorServiceImpl implements FornecedorService {

    private final String ERRO_SALVAR_FORNECEDOR = "Erro ao tentar salvar Fornecedor";

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private ModelMapper mapper;

	@Override
	@Transactional
	public Fornecedor salvarFornecedor(FornecedorDTO fornecedorDTO) {
		validarCnpjFornecedorParaSalvar(fornecedorDTO.getCnpj());
		Fornecedor fornecedor = mapper.map(fornecedorDTO, Fornecedor.class);
		fornecedor.setStatusFornecedor(Situacao.ATIVO);
		fornecedor.setDataHoraCadastro(LocalDateTime.now());

		Optional<Endereco> enderecoOptional = enderecoService.buscarEnderecoPorId(fornecedorDTO.getEndereco());
		if (enderecoOptional.isPresent()) {
			fornecedor.setEndereco(enderecoOptional.get());
		}

		return fornecedorRepository.save(fornecedor);
	}

    @Override
    @Transactional(readOnly = true)
    public List<Fornecedor> buscarTodosFornecedores() {
        return fornecedorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Fornecedor> buscarFornecedorPorCNPJ(String cnpj) {
        checarCnpjFornecedorExistente(cnpj);
        return fornecedorRepository.findFornecedorByCnpj(cnpj);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fornecedor> filtrarFornecedor(FornecedorDTO fornecedorDTO) {
        Example<Fornecedor> example = Example.of(mapper.map(fornecedorDTO, Fornecedor.class),
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return fornecedorRepository.findAll(example);
    }

	@Override
	@Transactional
	public Situacao alternarStatusFornecedor(String cnpj) {
		Optional<Fornecedor> fornecedorOptional = buscarFornecedorPorCNPJ(cnpj);
		if (fornecedorOptional.isPresent()) {
			if (fornecedorOptional.get().getStatusFornecedor().equals(Situacao.ATIVO)) {
				fornecedorOptional.get().setStatusFornecedor(Situacao.INATIVO);
			} else if (fornecedorOptional.get().getStatusFornecedor().equals(Situacao.INATIVO)) {
				fornecedorOptional.get().setStatusFornecedor(Situacao.ATIVO);
			}
			fornecedorRepository.save(fornecedorOptional.get());
			return fornecedorOptional.get().getStatusFornecedor();
		}
		return null;
	}

	@Override
	@Transactional
	public Fornecedor atualizarFornecedor(FornecedorDTO fornecedorDTO) {
		Optional<Fornecedor> fornecedorOptional = buscarFornecedorPorCNPJ(fornecedorDTO.getCnpj());
		if (fornecedorOptional.isPresent()) {
			LocalDateTime dateTime = fornecedorOptional.get().getDataHoraCadastro();
			Fornecedor fornecedor = mapper.map(fornecedorDTO, Fornecedor.class);
			fornecedor.setDataHoraCadastro(dateTime);

			Optional<Endereco> enderecoOptional = enderecoService.buscarEnderecoPorId(fornecedorDTO.getEndereco());
			if (enderecoOptional.isPresent()) {
				fornecedor.setEndereco(enderecoOptional.get());
			}
			return fornecedorRepository.save(fornecedor);
		}
		return null;
	}

    @Override
    @Transactional
    public void deletarFornecedor(String cnpj) {
        checarCnpjFornecedorExistente(cnpj);
        fornecedorRepository.deleteFornecedorByCnpj(cnpj);
    }

    @Override
    public void validarCnpjFornecedorParaSalvar(String cnpj) {
        if (fornecedorRepository.existsFornecedorByCnpj(cnpj)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_FORNECEDOR + ", CNPJ já cadastrado.");
        }
    }

    @Override
    public List<Fornecedor> filtrarFornecedoresPorCnpjDiferente(FornecedorDTO fornecedorDTO) {
        return fornecedorRepository.findByCnpjNot(fornecedorDTO.getCnpj());
    }

    @Override
    public void checarCnpjFornecedorExistente(String cnpj) {
        if (!fornecedorRepository.existsFornecedorByCnpj(cnpj)) {
            throw new ObjetoNaoEncontradoException("Fornecedor não encrontrado para o CNPJ informado.");
        }
    }

    @Override
    public boolean existeFornecedorPorIdEndereco(Integer idEndereco) {
        return fornecedorRepository.existsFornecedorByEnderecoId(idEndereco);
    }
}
