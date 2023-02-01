package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.VendaDTO;
import com.example.jbmotos.model.entity.Venda;
import com.example.jbmotos.model.repositories.VendaRepository;
import com.example.jbmotos.services.VendaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service
public class VendaServiceImpl implements VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public Venda salvarVenda(VendaDTO vendaDTO) {
        return vendaRepository.save(mapper.map(vendaDTO, Venda.class));
    }

    @Override
    public List<Venda> buscarTodasVendas() {
        return vendaRepository.findAll();
    }

    @Override
    public Optional<Venda> buscarVendaPorId(Integer id) {
        return vendaRepository.findById(id);
    }

    @Override
    public Venda atualizarVenda(VendaDTO vendaDTO) {
        Objects.requireNonNull(vendaDTO.getId(), "Erro ao tentar atualizar a Venda. Informe um Id.");
        return vendaRepository.save(mapper.map(vendaDTO, Venda.class));
    }

    @Override
    public void deletarVenda(Integer id) {
        vendaRepository.deleteById(id);
    }
}
