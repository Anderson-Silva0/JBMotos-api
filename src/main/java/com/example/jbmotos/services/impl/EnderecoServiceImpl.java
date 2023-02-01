package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.EnderecoDTO;
import com.example.jbmotos.model.entity.Endereco;
import com.example.jbmotos.model.repositories.EnderecoRepository;
import com.example.jbmotos.services.EnderecoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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
        return enderecoRepository.findById(id);
    }

    @Override
    @Transactional
    public Endereco atualizarEndereco(EnderecoDTO enderecoDTO) {
        Objects.requireNonNull(enderecoDTO.getId(), "Erro ao tentar atualizar o Endereço. Informe um Id.");
        return enderecoRepository.save(mapper.map(enderecoDTO, Endereco.class));
    }

    @Override
    @Transactional
    public void deletarEndereco(Integer id) {
        enderecoRepository.deleteById(id);
    }
}
