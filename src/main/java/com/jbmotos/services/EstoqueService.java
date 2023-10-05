package com.jbmotos.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.jbmotos.api.dto.EstoqueDTO;
import com.jbmotos.model.entity.Estoque;

public interface EstoqueService {

    Estoque salvarEstoque(EstoqueDTO estoqueDTO);

    List<Estoque> buscarTodosEstoques();

    Optional<Estoque> buscarEstoquePorId(Integer id);

    Estoque atualizarEstoque(EstoqueDTO estoqueDTO);

    void deletarEstoquePorId(Integer id);

    Integer obterQuantidadeDoProduto(Integer idProduto);

    void adicionarQuantidadeAoEstoque(Integer idProduto, Integer quantidade);
    
    BigDecimal calcularCustoTotalEstoque();

    BigDecimal calcularPotencialVendaEstoque();

    void validarEstoque(Integer id);

    void verificarUsoEstoque(Integer id);
}
