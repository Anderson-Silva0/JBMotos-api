package com.jbmotos.services;

import java.util.List;

import com.jbmotos.api.dto.EnderecoDTO;
import com.jbmotos.model.entity.Endereco;

public interface EnderecoService {

    Endereco salvarEndereco(EnderecoDTO enderecoDTO);

    List<Endereco> buscarTodosEnderecos();

    Endereco buscarEnderecoPorId(Integer id);

    Endereco atualizarEndereco(EnderecoDTO enderecoDTO);

    void deletarEnderecoPorId(Integer id);

    void validarEndereco(Integer id);
}
