package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.EnderecoDTO;
import com.example.jbmotos.model.entity.Endereco;
import com.example.jbmotos.model.repositories.EnderecoRepository;
import com.example.jbmotos.services.ClienteService;
import com.example.jbmotos.services.EnderecoService;
import com.example.jbmotos.services.FuncionarioService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EnderecoServiceImpl implements EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    @Lazy
    private ClienteService clienteService;

    @Autowired
    private FuncionarioService funcionarioService;

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
        if (!enderecoRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("Endereço não encontrado para o Id informado.");
        }
        return enderecoRepository.findById(id);
    }

    @Override
    @Transactional
    public Endereco atualizarEndereco(EnderecoDTO enderecoDTO) {
        if (!enderecoRepository.existsById(enderecoDTO.getId())) {
            throw new ObjetoNaoEncontradoException("Erro ao tentar atualizar." +
                    " Endereço não encontrado para o Id informado.");
        }
        return enderecoRepository.save(mapper.map(enderecoDTO, Endereco.class));
    }

    @Override
    @Transactional
    public void deletarEnderecoPorId(Integer id) {
        if (!enderecoRepository.existsById(id)){
            throw new ObjetoNaoEncontradoException("Erro ao tentar deletar." +
                    " Endereço não encontrado para o Id informado.");
        }
        verificaSeEnderecoPertenceAAlgumClienteOuFuncionario(id);
        enderecoRepository.deleteById(id);
    }

    @Override
    public boolean existeEnderecoPorId(Integer id) {
        return enderecoRepository.existsById(id);
    }

    @Override
    public void verificaSeEnderecoPertenceAAlgumClienteOuFuncionario(Integer id) {
        clienteService.buscarTodosClientes().stream().forEach(cliente -> {
            if (id == cliente.getEndereco().getId()) {
                throw new RegraDeNegocioException("Erro ao tentar deletar, o Endereço pertence a um Cliente.");
            }
        });
        funcionarioService.buscarTodosFuncionarios().stream().forEach(funcionario -> {
            if (id == funcionario.getEndereco().getId()) {
                throw new RegraDeNegocioException("Erro ao tentar deletar, o Endereço pertence a um Funcionário.");
            }
        });
    }
}
