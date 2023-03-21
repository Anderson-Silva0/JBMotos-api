package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.EnderecoDTO;
import com.example.jbmotos.model.entity.Endereco;

import java.util.List;
import java.util.Optional;

public interface EnderecoService {
    Endereco salvarEndereco(EnderecoDTO enderecoDTO);

    List<Endereco> buscarTodosEnderecos();

    Optional<Endereco> buscarEnderecoPorId(Integer id);

    Endereco atualizarEndereco(EnderecoDTO enderecoDTO);

    void deletarEnderecoPorId(Integer id);

    void validarEndereco(Integer id);

    void verificarUsoEndereco(Integer idEndereco);
}
