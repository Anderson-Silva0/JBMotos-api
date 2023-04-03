package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.ProdutoDTO;
import com.example.jbmotos.model.entity.Produto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProdutoService {

    Produto salvarProduto(ProdutoDTO produtoDTO);

    List<Produto> buscarTodosProdutos();

    Optional<Produto> buscarProdutoPorId(Integer id);

    Produto atualizarProduto(ProdutoDTO produtoDTO);

    void deletarProduto(Integer id);

    BigDecimal calcularLucroProduto(Integer idProduto);

    void verificaSeProdutoExiste(Integer id);

    boolean existeProdutoPorIdEstoque(Integer idEstoque);
}
