package com.example.jbmotos.services;

import java.util.List;
import java.util.Optional;

import com.example.jbmotos.api.dto.EnderecoDTO;
import com.example.jbmotos.model.entity.Endereco;

public interface EnderecoService {
	
    Endereco salvarEndereco(EnderecoDTO enderecoDTO);

    List<Endereco> buscarTodosEnderecos();

    Optional<Endereco> buscarEnderecoPorId(Integer id);

    Endereco atualizarEndereco(EnderecoDTO enderecoDTO);

    void deletarEnderecoPorId(Integer id);

    void validarEndereco(Integer id);
}
