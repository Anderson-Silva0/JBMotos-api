package com.jbmotos.services;

import java.math.BigDecimal;
import java.util.List;

import com.jbmotos.api.dto.VendaDTO;
import com.jbmotos.model.entity.Produto;
import com.jbmotos.model.entity.Venda;

public interface VendaService {

    Venda salvarVenda(VendaDTO vendaDTO);

    List<Venda> buscarTodasVendas();

    Venda buscarVendaPorId(Integer id);

    List<Venda> filtrarVenda(VendaDTO vendaDTO);

    Venda atualizarVenda(VendaDTO vendaDTO);

    void deletarVenda(Integer id);

    BigDecimal calcularLucroDaVenda(Integer idVenda);

    void validarVenda(Integer id);

    List<Produto> buscarProdutosDaVenda(Integer idVenda);
}
