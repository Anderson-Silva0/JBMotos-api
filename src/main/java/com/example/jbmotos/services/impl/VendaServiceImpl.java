package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.VendaDTO;
import com.example.jbmotos.model.entity.Venda;
import com.example.jbmotos.model.repositories.VendaRepository;
import com.example.jbmotos.services.ClienteService;
import com.example.jbmotos.services.FuncionarioService;
import com.example.jbmotos.services.VendaService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
public class VendaServiceImpl implements VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Venda salvarVenda(VendaDTO vendaDTO) {
        vendaDTO.setData_hora(LocalDateTime.now());
        Venda venda = mapper.map(vendaDTO, Venda.class);
        venda.setCliente(clienteService.buscarClientePorCPF(vendaDTO.getCpf_cliente()).get());
        venda.setFuncionario(funcionarioService.buscarFuncionarioPorCPF(vendaDTO.getCpf_funcionario()).get());
        return vendaRepository.save(venda);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venda> buscarTodasVendas() {
        return vendaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venda> buscarVendaPorId(Integer id) {
        verificaSeVendaExiste(id);
        return vendaRepository.findById(id);
    }

    @Override
    @Transactional
    public Venda atualizarVenda(VendaDTO vendaDTO) {
        LocalDateTime dateTime = buscarVendaPorId(vendaDTO.getId()).get().getData_hora();
        Venda venda = mapper.map(vendaDTO, Venda.class);
        venda.setCliente(clienteService.buscarClientePorCPF(vendaDTO.getCpf_cliente()).get());
        venda.setFuncionario(funcionarioService.buscarFuncionarioPorCPF(vendaDTO.getCpf_funcionario()).get());
        venda.setData_hora(dateTime);
        return vendaRepository.save(venda);
    }

    @Override
    @Transactional
    public void deletarVenda(Integer id) {
        verificaSeVendaExiste(id);
        vendaRepository.deleteById(id);
    }

    @Override
    public void verificaSeVendaExiste(Integer id) {
        if (!vendaRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("Venda não encontrada para o Id informado.");
        }
    }
}
