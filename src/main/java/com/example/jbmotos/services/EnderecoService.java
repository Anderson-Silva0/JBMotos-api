package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.EnderecoDTO;
import com.example.jbmotos.model.entity.Endereco;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EnderecoService {
    Endereco salvarEndereco(EnderecoDTO enderecoDTO);

    List<Endereco> buscarTodosEnderecos();

    Optional<Endereco> buscarEnderecoPorId(Integer id);

    Endereco atualizarEndereco(EnderecoDTO enderecoDTO);

    @Transactional
    void deletarEnderecoPorId(Integer id);

    boolean existeEnderecoPorId(Integer id);

    void verificaSeEnderecoPertenceAAlgumClienteOuFuncionario(Integer id);
}
