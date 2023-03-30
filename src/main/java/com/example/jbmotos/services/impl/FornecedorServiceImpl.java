package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.FornecedorDTO;
import com.example.jbmotos.model.entity.Fornecedor;
import com.example.jbmotos.model.repositories.FornecedorRepository;
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

import java.util.List;
import java.util.Optional;

@Service
public class FornecedorServiceImpl implements FornecedorService {

    private final String ERRO_SALVAR_FORNECEDOR = "Erro ao tentar salvar Fornecedor";
    private final String ERRO_ATUALIZAR_FORNECEDOR = "Erro ao tentar atualizar Fornecedor";

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    @Lazy
    private ClienteService clienteService;

    @Autowired
    @Lazy
    private FuncionarioService funcionarioService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Fornecedor salvarFornecedor(FornecedorDTO fornecedorDTO) {
        validarCnpjFornecedorParaSalvar(fornecedorDTO.getCnpj());
        validarEnderecoParaSalvar(fornecedorDTO.getEndereco());

        Fornecedor fornecedor = mapper.map(fornecedorDTO, Fornecedor.class);
        fornecedor.setEndereco(enderecoService.buscarEnderecoPorId(fornecedorDTO.getEndereco()).get());
        return fornecedorRepository.save(fornecedor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fornecedor> buscarTodosFornecedores() {
        return fornecedorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Fornecedor> buscarFornecedorPorCNPJ(String cnpj) {
        checarCnpjFornecedorExistente(cnpj);
        return fornecedorRepository.findFornecedorByCnpj(cnpj);
    }

    @Override
    @Transactional
    public Fornecedor atualizarFornecedor(FornecedorDTO fornecedorDTO) {
        checarCnpjFornecedorExistente(fornecedorDTO.getCnpj());
        validarEnderecoParaAtualizar(fornecedorDTO);

        Fornecedor fornecedor = mapper.map(fornecedorDTO, Fornecedor.class);
        fornecedor.setEndereco(enderecoService.buscarEnderecoPorId(fornecedorDTO.getEndereco()).get());
        return fornecedorRepository.save(fornecedor);
    }

    @Override
    @Transactional
    public void deletarFornecedor(String cnpj) {
        checarCnpjFornecedorExistente(cnpj);
        fornecedorRepository.deleteFornecedorByCnpj(cnpj);
    }

    @Override
    public void validarEnderecoParaSalvar(Integer idEndereco){
        if (existeFornecedorPorIdEndereco(idEndereco)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_FORNECEDOR+", o Endereço já pertence a um Fornecedor.");
        }
        if (clienteService.existeClientePorIdEndereco(idEndereco)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_FORNECEDOR+", o Endereço já pertence a um Cliente.");
        }
        if (funcionarioService.existeFuncionarioPorIdEndereco(idEndereco)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_FORNECEDOR+", o Endereço já pertence a um Funcionário.");
        }
    }

    @Override
    public void validarCnpjFornecedorParaSalvar(String cnpj){
        if (fornecedorRepository.existsFornecedorByCnpj(cnpj)) {
            throw new RegraDeNegocioException(ERRO_SALVAR_FORNECEDOR+", CNPJ já cadastrado.");
        }
    }

    @Override
    public void validarEnderecoParaAtualizar(FornecedorDTO fornecedorDTO) {
        filtrarFornecedoresPorCnpjDiferente(fornecedorDTO).stream().forEach(fornecedorFiltrado -> {
            if (fornecedorDTO.getEndereco() == fornecedorFiltrado.getEndereco().getId()) {
                throw new RegraDeNegocioException(ERRO_ATUALIZAR_FORNECEDOR+", o Endereço já pertence a um " +
                        "Fornecedor.");
            }
            if (clienteService.existeClientePorIdEndereco(fornecedorDTO.getEndereco())) {
                throw new RegraDeNegocioException(ERRO_ATUALIZAR_FORNECEDOR+", o Endereço já pertence a um Cliente.");
            }
            if (funcionarioService.existeFuncionarioPorIdEndereco(fornecedorDTO.getEndereco())) {
                throw new RegraDeNegocioException(ERRO_ATUALIZAR_FORNECEDOR+", o Endereço já pertence a um " +
                        "Funcionário.");
            }
        });
    }

    @Override
    public List<Fornecedor> filtrarFornecedoresPorCnpjDiferente(FornecedorDTO fornecedorDTO) {
        return fornecedorRepository.findByCnpjNot(fornecedorDTO.getCnpj());
    }

    @Override
    public void checarCnpjFornecedorExistente(String cnpj){
        if (!fornecedorRepository.existsFornecedorByCnpj(cnpj)) {
            throw new ObjetoNaoEncontradoException("Fornecedor não encrontrado para o CNPJ informado.");
        }
    }

    @Override
    public boolean existeFornecedorPorIdEndereco(Integer idEndereco) {
        return fornecedorRepository.existsFornecedorByEnderecoId(idEndereco);
    }
}
