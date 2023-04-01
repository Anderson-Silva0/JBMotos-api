package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.FuncionarioDTO;
import com.example.jbmotos.model.entity.Funcionario;
import com.example.jbmotos.model.repositories.FuncionarioRepository;
import com.example.jbmotos.services.ClienteService;
import com.example.jbmotos.services.EnderecoService;
import com.example.jbmotos.services.FornecedorService;
import com.example.jbmotos.services.FuncionarioService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FuncionarioServiceImpl implements FuncionarioService {

    private final String ERRO_SALVAR_FUNCIONARIO = "Erro ao tentar salvar Funcionário";
    private final String ERRO_ATUALIZAR_FUNCIONARIO = "Erro ao tentar atualizar Funcionário";

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    @Lazy
    private EnderecoService enderecoService;

    @Autowired
    @Lazy
    private ClienteService clienteService;

    @Autowired
    private FornecedorService fornecedorService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Funcionario salvarFuncionario(FuncionarioDTO funcionarioDTO){
        funcionarioDTO.setDataHoraCadastro(LocalDateTime.now());
        validarCpfFuncionarioParaSalvar(funcionarioDTO.getCpf());
        validarEnderecoParaSalvar(funcionarioDTO.getEndereco());

        Funcionario funcionario = mapper.map(funcionarioDTO, Funcionario.class);
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
        checarCpfFuncionarioExistente(funcionarioDTO.getCpf());
        validarEnderecoParaAtualizar(funcionarioDTO);

        Funcionario funcionario = mapper.map(funcionarioDTO, Funcionario.class);
        funcionario.setEndereco(enderecoService.buscarEnderecoPorId(funcionarioDTO.getEndereco()).get());
        funcionario.setDataHoraCadastro(dateTime);
        return funcionarioRepository.save(funcionario);
    }

    @Override
    @Transactional
    public void deletarFuncionario(String cpf){
        checarCpfFuncionarioExistente(cpf);
        funcionarioRepository.deleteFuncionarioByCpf(cpf);
    }

    @Override
    public void validarEnderecoParaSalvar(Integer idEndereco){
        if (existeFuncionarioPorIdEndereco(idEndereco)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_FUNCIONARIO+", o Endereço já pertence a um Funcionário.");
        }
        if (clienteService.existeClientePorIdEndereco(idEndereco)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_FUNCIONARIO+", o Endereço já pertence a um Cliente.");
        }
        if (fornecedorService.existeFornecedorPorIdEndereco(idEndereco)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_FUNCIONARIO+", o Endereço já pertence a um Fornecedor.");
        }
    }

    @Override
    public void validarCpfFuncionarioParaSalvar(String cpf){
        if (funcionarioRepository.existsFuncionarioByCpf(cpf)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_FUNCIONARIO+", CPF já cadastrado.");
        }
    }

    @Override
    public void validarEnderecoParaAtualizar(FuncionarioDTO funcionarioDTO) {
        filtrarFuncionariosPorCpfDiferente(funcionarioDTO).stream().forEach(funcionarioFiltrado -> {
            if (funcionarioDTO.getEndereco() == funcionarioFiltrado.getEndereco().getId()) {
                throw new RegraDeNegocioException(ERRO_ATUALIZAR_FUNCIONARIO+", o Endereço já pertence a um " +
                        "Funcionário.");
            }
            if (clienteService.existeClientePorIdEndereco(funcionarioDTO.getEndereco())) {
                throw new RegraDeNegocioException(ERRO_ATUALIZAR_FUNCIONARIO+", o Endereço já pertence a um Cliente.");
            }
            if (fornecedorService.existeFornecedorPorIdEndereco(funcionarioDTO.getEndereco())) {
                throw new RegraDeNegocioException(ERRO_ATUALIZAR_FUNCIONARIO+", o Endereço já pertence a um " +
                        "Fornecedor.");
            }
        });
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
