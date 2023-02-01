package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.FuncionarioDTO;
import com.example.jbmotos.model.entity.Funcionario;
import com.example.jbmotos.model.repositories.FuncionarioRepository;
import com.example.jbmotos.services.FuncionarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FuncionarioServiceImpl implements FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Funcionario salvarFuncionario(FuncionarioDTO funcionarioDTO) {
        return funcionarioRepository.save(mapper.map(funcionarioDTO, Funcionario.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Funcionario> buscarTodosFuncionarios() {
        return funcionarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Funcionario> buscarFuncionarioPorCPF(String cpf) {
        return funcionarioRepository.findFuncionarioByCpf(cpf);
    }

    @Override
    @Transactional
    public Funcionario atualizarFuncionario(FuncionarioDTO funcionarioDTO) {
        Objects.requireNonNull(funcionarioDTO.getCpf(), "Erro ao tentar atualizar o Funcionário. Informe um CPF.");
        return funcionarioRepository.save(mapper.map(funcionarioDTO, Funcionario.class));
    }

    @Override
    @Transactional
    public void deletarFuncionario(String cpf) {
        funcionarioRepository.deleteFuncionarioByCpf(cpf);
    }
}
