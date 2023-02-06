package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.FuncionarioDTO;
import com.example.jbmotos.model.entity.Funcionario;
import com.example.jbmotos.model.repositories.FuncionarioRepository;
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
public class FuncionarioServiceImpl implements FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    @Lazy
    private EnderecoService enderecoService;

    @Autowired
    @Lazy
    private ClienteService clienteService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Funcionario salvarFuncionario(FuncionarioDTO funcionarioDTO) {
        existeFuncionarioPorCpfParaSalvar(funcionarioDTO.getCpf());
        validaEnderecoParaSalvar(funcionarioDTO);
        Funcionario funcionario = mapper.map(funcionarioDTO, Funcionario.class);
        funcionario.setEndereco(enderecoService.buscarEnderecoPorId(funcionarioDTO.getEndereco()).get());
        return funcionarioRepository.save(funcionario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Funcionario> buscarTodosFuncionarios() {
        return funcionarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Funcionario> buscarFuncionarioPorCPF(String cpf) {
        verificaSeFuncionarioExiste(cpf);
        return funcionarioRepository.findFuncionarioByCpf(cpf);
    }

    @Override
    @Transactional
    public Funcionario atualizarFuncionario(FuncionarioDTO funcionarioDTO) {
        verificaSeFuncionarioExiste(funcionarioDTO.getCpf());
        validaEnderecoParaAtualizar(funcionarioDTO);
        Funcionario funcionario = mapper.map(funcionarioDTO, Funcionario.class);
        funcionario.setEndereco(enderecoService.buscarEnderecoPorId(funcionarioDTO.getEndereco()).get());
        return funcionarioRepository.save(funcionario);
    }

    @Override
    @Transactional
    public void deletarFuncionario(String cpf) {
        verificaSeFuncionarioExiste(cpf);
        funcionarioRepository.deleteFuncionarioByCpf(cpf);
    }

    @Override
    public void validaEnderecoParaSalvar(FuncionarioDTO funcionarioDTO) {
            buscarTodosFuncionarios().stream().forEach(funcionarioFiltrado -> {
                if (funcionarioDTO.getEndereco() == funcionarioFiltrado.getEndereco().getId())
                    throw new RegraDeNegocioException("Erro ao tentar salvar Funcionário," +
                            " o Endereço já pertence a um Funcionário.");
            });
            clienteService.buscarTodosClientes().stream().forEach(c -> {
                if (funcionarioDTO.getEndereco() == c.getEndereco().getId())
                    throw new RegraDeNegocioException("Erro ao tentar salvar Funcionário," +
                            " o Endereço já pertence a um Cliente.");
            });
    }

    @Override
    public void validaEnderecoParaAtualizar(FuncionarioDTO funcionarioDTO) {
        Funcionario funcionario = buscarFuncionarioPorCPF(funcionarioDTO.getCpf()).get();
            buscarTodosFuncionarios().stream().filter(f -> funcionario.getCpf() != f.getCpf() )
                    .forEach(funcionarioFiltrado -> {
                if (funcionarioDTO.getEndereco() == funcionarioFiltrado.getEndereco().getId())
                    throw new RegraDeNegocioException("Erro ao tentar atualizar Funcionário," +
                            " o Endereço já pertence a um Funcionário.");
            });
            clienteService.buscarTodosClientes().stream().forEach(c -> {
                if (funcionarioDTO.getEndereco() == c.getEndereco().getId())
                    throw new RegraDeNegocioException("Erro ao tentar atualizar Funcionário," +
                            " o Endereço já pertence a um Cliente.");
            });
    }

    @Override
    public void existeFuncionarioPorCpfParaSalvar(String cpf) {
        if (funcionarioRepository.existsFuncionarioByCpf(cpf))
            throw new RegraDeNegocioException("Erro ao tentar salvar Funcionário, CPF já cadastrado.");
    }

    @Override
    public void verificaSeFuncionarioExiste(String cpf) {
        if (!funcionarioRepository.existsFuncionarioByCpf(cpf))
            throw new ObjetoNaoEncontradoException("Funcionário não encrontrado para o CPF informado.");
    }
}
