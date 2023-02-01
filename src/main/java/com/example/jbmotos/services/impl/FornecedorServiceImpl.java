package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.FornecedorDTO;
import com.example.jbmotos.model.entity.Fornecedor;
import com.example.jbmotos.model.repositories.FornecedorRepository;
import com.example.jbmotos.services.FornecedorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FornecedorServiceImpl implements FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Fornecedor salvarFornecedor(FornecedorDTO fornecedorDTO) {
        return fornecedorRepository.save(mapper.map(fornecedorDTO, Fornecedor.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fornecedor> buscarTodosFornecedores() {
        return fornecedorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Fornecedor> buscarFornecedorPorCNPJ(String cnpj) {
        return fornecedorRepository.findFornecedorByCnpj(cnpj);
    }

    @Override
    @Transactional
    public Fornecedor atualizarFornecedor(FornecedorDTO fornecedorDTO) {
        Objects.requireNonNull(fornecedorDTO.getCnpj(), "Erro ao tentar atualizar o Fornecedor. Informe um CNPJ.");
        return fornecedorRepository.save(mapper.map(fornecedorDTO, Fornecedor.class));
    }

    @Override
    @Transactional
    public void deletarFornecedor(String cnpj) {
        fornecedorRepository.deleteFornecedorByCnpj(cnpj);
    }
}
