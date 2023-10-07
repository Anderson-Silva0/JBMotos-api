package com.jbmotos.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.ProdutoVendaDTO;
import com.jbmotos.model.entity.ProdutoVenda;

public interface ProdutoVendaService {

    ProdutoVenda salvarProdutoVenda(ProdutoVendaDTO produtoVendaDTO);

    List<ProdutoVenda> buscarTodosProdutoVenda();

    ProdutoVenda buscarProdutoVendaPorId(Integer id);

    ProdutoVenda atualizarProdutoVenda(ProdutoVendaDTO produtoVendaDTO);

    void deletarProdutoVendaPorId(Integer id);

    @Transactional(readOnly = true)
    List<ProdutoVenda> buscarProdutoVendaPorIdVenda(Integer idVenda);

    void validarProdutoVenda(Integer id);

    void atualizarQtdEstoqueParaDeletar(Integer id);
}
