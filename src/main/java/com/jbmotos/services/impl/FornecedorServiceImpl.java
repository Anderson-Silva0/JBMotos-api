package com.jbmotos.services.impl;

import java.time.LocalDateTime;
import java.util.List;

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

	private final String FORNECEDOR_NAO_ENCONTRADO = "Fornecedor não encrontrado para o CNPJ informado.";

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

		Endereco endereco = enderecoService.buscarEnderecoPorId(fornecedorDTO.getEndereco());
		fornecedor.setEndereco(endereco);

		return fornecedorRepository.save(fornecedor);
	}

    @Override
    @Transactional(readOnly = true)
    public List<Fornecedor> buscarTodosFornecedores() {
        return fornecedorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Fornecedor buscarFornecedorPorCNPJ(String cnpj) {
        return fornecedorRepository.findFornecedorByCnpj(cnpj)
        		.orElseThrow(() -> new ObjetoNaoEncontradoException(FORNECEDOR_NAO_ENCONTRADO));
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
		Fornecedor fornecedor = buscarFornecedorPorCNPJ(cnpj);
		if (fornecedor.getStatusFornecedor().equals(Situacao.ATIVO)) {
			fornecedor.setStatusFornecedor(Situacao.INATIVO);
		} else if (fornecedor.getStatusFornecedor().equals(Situacao.INATIVO)) {
			fornecedor.setStatusFornecedor(Situacao.ATIVO);
		}
		fornecedorRepository.save(fornecedor);
		return fornecedor.getStatusFornecedor();
	}

	@Override
	@Transactional
	public Fornecedor atualizarFornecedor(FornecedorDTO fornecedorDTO) {
		Fornecedor fornecedor = mapper.map(fornecedorDTO, Fornecedor.class);

		LocalDateTime dateTime = buscarFornecedorPorCNPJ(fornecedorDTO.getCnpj()).getDataHoraCadastro();
		fornecedor.setDataHoraCadastro(dateTime);

		Endereco endereco = enderecoService.buscarEnderecoPorId(fornecedorDTO.getEndereco());
		fornecedor.setEndereco(endereco);

		return fornecedorRepository.save(fornecedor);
	}

	@Override
	@Transactional
	public void deletarFornecedor(String cnpj) {
		if (!fornecedorRepository.existsFornecedorByCnpj(cnpj)) {
			throw new ObjetoNaoEncontradoException(FORNECEDOR_NAO_ENCONTRADO);
		}
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
    public boolean existeFornecedorPorIdEndereco(Integer idEndereco) {
        return fornecedorRepository.existsFornecedorByEnderecoId(idEndereco);
    }
}
