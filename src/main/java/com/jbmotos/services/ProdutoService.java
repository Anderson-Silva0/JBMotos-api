package com.jbmotos.services;

import java.math.BigDecimal;
import java.util.List;

import com.jbmotos.api.dto.ProdutoDTO;
import com.jbmotos.model.entity.Produto;
import com.jbmotos.model.enums.Situacao;

public interface ProdutoService {

    Produto salvarProduto(ProdutoDTO produtoDTO);

    List<Produto> buscarTodosProdutos();

    Produto buscarProdutoPorId(Integer id);

    List<Produto> filtrarProduto(ProdutoDTO produtoDTO);

    Situacao alternarStatusProduto(Integer idProduto);

    Produto atualizarProduto(ProdutoDTO produtoDTO);

    void deletarProduto(Integer id);

    BigDecimal calcularLucroProduto(Integer idProduto);

    void verificaSeProdutoExiste(Integer id);

    boolean existeProdutoPorIdEstoque(Integer idEstoque);
}
