package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.VendaDTO;
import com.example.jbmotos.model.entity.Venda;
import com.example.jbmotos.services.VendaService;

import java.util.List;
import java.util.Optional;

public class VendaServiceImpl implements VendaService {

    @Override
    public Venda salvarVenda(VendaDTO VendaDTO) {
        return null;
    }

    @Override
    public List<Venda> buscarTodasVendas() {
        return null;
    }

    @Override
    public Optional<Venda> buscarVendaPorId(Integer id) {
        return Optional.empty();
    }

    @Override
    public Venda atualizarVenda(VendaDTO VendaDTO) {
        return null;
    }

    @Override
    public void deletarVenda(Integer id) {

    }
}
