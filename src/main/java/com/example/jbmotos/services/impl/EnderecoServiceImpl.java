package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.EnderecoDTO;
import com.example.jbmotos.model.entity.Endereco;
import com.example.jbmotos.model.repositories.EnderecoRepository;
import com.example.jbmotos.services.EnderecoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EnderecoServiceImpl implements EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

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
        Optional<Endereco> endereco = enderecoRepository.findById(id);
        if (endereco.isEmpty()){
            throw new ObjetoNaoEncontradoException("Endereço não encontrado para o Id informado.");
        }
        return endereco;
    }

    @Override
    @Transactional
    public Endereco atualizarEndereco(EnderecoDTO enderecoDTO) {
        Optional<Endereco> endereco = enderecoRepository.findById(enderecoDTO.getId());
        if (endereco.isEmpty()){
            throw new ObjetoNaoEncontradoException("Erro ao tentar atualizar." +
                    " Endereço não encontrado para o Id informado.");
        }
        return enderecoRepository.save(mapper.map(enderecoDTO, Endereco.class));
    }

    @Override
    @Transactional
    public void deletarEnderecoPorId(Integer id) {
        Optional<Endereco> endereco = enderecoRepository.findById(id);
        if (endereco.isEmpty()){
            throw new ObjetoNaoEncontradoException("Erro ao tentar deletar." +
                    " Endereço não encontrado para o Id informado.");
        }
        enderecoRepository.deleteById(id);
    }
}
