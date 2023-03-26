package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.EstoqueDTO;
import com.example.jbmotos.model.entity.Estoque;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EstoqueService {

    Estoque salvarEstoque(EstoqueDTO estoqueDTO);

    List<Estoque> buscarTodosEstoques();

    Optional<Estoque> buscarEstoquePorId(Integer id);

    Estoque atualizarEstoque(EstoqueDTO estoqueDTO);

    void deletarEstoquePorId(Integer id);

    Integer obterQuantidadeDoProduto(Integer idProduto);

    void adicionarQuantidadeAoEstoque(Integer idProduto, Integer quantidade);

    BigDecimal calcularValorTotalEstoque();

    void validarEstoque(Integer id);

    void verificarUsoEstoque(Integer id);
}
