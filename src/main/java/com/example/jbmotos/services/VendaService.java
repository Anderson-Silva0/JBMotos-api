package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.VendaDTO;
import com.example.jbmotos.model.entity.Venda;

import java.util.List;
import java.util.Optional;

public interface VendaService {
    
    Venda salvarVenda(VendaDTO vendaDTO);

    List<Venda> buscarTodasVendas();

    Optional<Venda> buscarVendaPorId(Integer id);

    Venda atualizarVenda(VendaDTO vendaDTO);

    void deletarVenda(Integer id);
}
