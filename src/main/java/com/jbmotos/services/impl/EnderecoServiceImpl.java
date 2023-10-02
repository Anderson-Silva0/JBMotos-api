package com.jbmotos.services.impl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.EnderecoDTO;
import com.jbmotos.model.entity.Endereco;
import com.jbmotos.model.repositories.EnderecoRepository;
import com.jbmotos.services.ClienteService;
import com.jbmotos.services.EnderecoService;
import com.jbmotos.services.FornecedorService;
import com.jbmotos.services.FuncionarioService;
import com.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.jbmotos.services.exception.RegraDeNegocioException;

@Service
public class EnderecoServiceImpl implements EnderecoService {

    private final String ERRO_DELETAR_ENDERECO = "Erro ao tentar deletar, o Endereço pertence a um";

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    @Lazy
    private ClienteService clienteService;

    @Autowired
    @Lazy
    private FuncionarioService funcionarioService;

    @Autowired
    @Lazy
    private FornecedorService fornecedorService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Endereco salvarEndereco(EnderecoDTO enderecoDTO) {
        return enderecoRepository.save(mapper.map(enderecoDTO, Endereco.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Endereco> buscarTodosEnderecos() {
        return enderecoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Endereco> buscarEnderecoPorId(Integer id) {
        validarEndereco(id);
        return enderecoRepository.findById(id);
    }

    @Override
    @Transactional
    public Endereco atualizarEndereco(EnderecoDTO enderecoDTO) {
        validarEndereco(enderecoDTO.getId());
        return enderecoRepository.save(mapper.map(enderecoDTO, Endereco.class));
    }

    @Override
    @Transactional
    public void deletarEnderecoPorId(Integer id) {
        validarEndereco(id);
        verificarUsoEndereco(id);
        enderecoRepository.deleteById(id);
    }

    @Override
    public void validarEndereco(Integer id) {
        if(!enderecoRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("Endereço não encontrado para o Id informado.");
        }
    }

    private void verificarUsoEndereco(Integer idEndereco) {
        if (clienteService.existeClientePorIdEndereco(idEndereco)) {
            throw new RegraDeNegocioException(ERRO_DELETAR_ENDERECO+" Cliente.");
        }
        if (funcionarioService.existeFuncionarioPorIdEndereco(idEndereco)) {
            throw new RegraDeNegocioException(ERRO_DELETAR_ENDERECO+" Funcionário.");
        }
        if (fornecedorService.existeFornecedorPorIdEndereco(idEndereco)) {
            throw new RegraDeNegocioException(ERRO_DELETAR_ENDERECO+" Fornecedor.");
        }
    }
}
