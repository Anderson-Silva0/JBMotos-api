package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.FuncionarioDTO;
import com.example.jbmotos.model.entity.Funcionario;
import com.example.jbmotos.model.repositories.FuncionarioRepository;
import com.example.jbmotos.services.EnderecoService;
import com.example.jbmotos.services.FuncionarioService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public Funcionario salvarFuncionario(FuncionarioDTO funcionarioDTO){
        validarCpfFuncionarioParaSalvar(funcionarioDTO.getCpf());
        Funcionario funcionario = mapper.map(funcionarioDTO, Funcionario.class);
        funcionario.setDataHoraCadastro(LocalDateTime.now());
        funcionario.setEndereco(enderecoService.buscarEnderecoPorId(funcionarioDTO.getEndereco()).get());
        return funcionarioRepository.save(funcionario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Funcionario> buscarTodosFuncionarios(){
        return funcionarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Funcionario> buscarFuncionarioPorCPF(String cpf){
        checarCpfFuncionarioExistente(cpf);
        return funcionarioRepository.findFuncionarioByCpf(cpf);
    }

    @Override
    @Transactional
    public Funcionario atualizarFuncionario(FuncionarioDTO funcionarioDTO){
        LocalDateTime dateTime = buscarFuncionarioPorCPF(funcionarioDTO.getCpf()).get().getDataHoraCadastro();
        Funcionario funcionario = mapper.map(funcionarioDTO, Funcionario.class);
        funcionario.setDataHoraCadastro(dateTime);
        funcionario.setEndereco(enderecoService.buscarEnderecoPorId(funcionarioDTO.getEndereco()).get());
        return funcionarioRepository.save(funcionario);
    }

    @Override
    @Transactional
    public void deletarFuncionario(String cpf){
        checarCpfFuncionarioExistente(cpf);
        funcionarioRepository.deleteFuncionarioByCpf(cpf);
    }

    @Override
    public void validarCpfFuncionarioParaSalvar(String cpf){
        if (funcionarioRepository.existsFuncionarioByCpf(cpf)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_FUNCIONARIO+", CPF já cadastrado.");
        }
    }

    @Override
    public List<Funcionario> filtrarFuncionariosPorCpfDiferente(FuncionarioDTO funcionarioDTO) {
        return funcionarioRepository.findByCpfNot(funcionarioDTO.getCpf());
    }

    @Override
    public void checarCpfFuncionarioExistente(String cpf){
        if (!funcionarioRepository.existsFuncionarioByCpf(cpf)) {
            throw new ObjetoNaoEncontradoException("Funcionário não encrontrado para o CPF informado.");
        }
    }

    @Override
    public boolean existeFuncionarioPorIdEndereco(Integer idEndereco){
        return funcionarioRepository.existsFuncionarioByEnderecoId(idEndereco);
    }
}
