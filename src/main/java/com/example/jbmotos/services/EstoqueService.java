package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.EstoqueDTO;
import com.example.jbmotos.model.entity.Estoque;

import java.util.List;
import java.util.Optional;

public interface EstoqueService {

    Estoque salvarEndereco(EstoqueDTO estoqueDTO);

    List<Estoque> buscarTodosEndereco();

    Optional<Estoque> buscarEnderecoPorId(Integer id);

    Estoque atualizarEstoque(EstoqueDTO estoqueDTO);

    void deletarEstoquePorId(Integer id);

    void validarEstoque(Integer id);
}
