package com.jbmotos.services;

import com.jbmotos.api.dto.PagamentoCartaoDTO;
import com.jbmotos.model.entity.PagamentoCartao;

import java.util.List;

public interface PagamentoCartaoService {

    PagamentoCartao salvarPagamentoCartao(PagamentoCartaoDTO pagamentoCartaoDTO);

    List<PagamentoCartao> buscarTodosPagamentosCartoes();

    PagamentoCartao buscarPagamentoCartaoPorId(Integer id);

    PagamentoCartao atualizarPagamentoCartao(PagamentoCartaoDTO pagamentoCartaoDTO);

    void deletarPagamentoCartao(Integer id);

    void existePagamentoCartao(Integer id);
}
