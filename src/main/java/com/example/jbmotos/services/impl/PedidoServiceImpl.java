package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.PedidoDTO;
import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.model.entity.Produto;
import com.example.jbmotos.model.entity.ProdutoPedido;
import com.example.jbmotos.model.repositories.PedidoRepository;

import com.example.jbmotos.services.*;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoPedidoService produtoPedidoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Pedido salvarPedido(PedidoDTO pedidoDTO) {
        pedidoDTO.setDataHoraCadastro(LocalDateTime.now());
        Pedido pedido = mapper.map(pedidoDTO, Pedido.class);
        pedido.setCliente(clienteService.buscarClientePorCPF(pedidoDTO.getCpfCliente()).get());
        pedido.setFuncionario(funcionarioService.buscarFuncionarioPorCPF(pedidoDTO.getCpfFuncionario()).get());
        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarTodosPedidos() {
        return pedidoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPedidoPorId(Integer id) {
        validarPedido(id);
        return pedidoRepository.findById(id);
    }

    @Override
    @Transactional
    public Pedido atualizarPedido(PedidoDTO pedidoDTO) {
        LocalDateTime dateTime = buscarPedidoPorId(pedidoDTO.getId()).get().getDataHoraCadastro();
        Pedido pedido = mapper.map(pedidoDTO, Pedido.class);
        pedido.setCliente(clienteService.buscarClientePorCPF(pedidoDTO.getCpfCliente()).get());
        pedido.setFuncionario(funcionarioService.buscarFuncionarioPorCPF(pedidoDTO.getCpfFuncionario()).get());
        pedido.setDataHoraCadastro(dateTime);
        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public void deletarPedido(Integer id) {
        validarPedido(id);
        Pedido pedido = buscarPedidoPorId(id).get();
        pedido.getProdutosPedido().stream().forEach(produto -> {
            produtoPedidoService.atualizarQtdEstoqueParaDeletar(produto.getId());
        });
        pedidoRepository.deleteById(id);
    }

    @Override
    public BigDecimal calcularLucroDoPedido(Integer idPedido){
        validarPedido(idPedido);
        List<ProdutoPedido> produtosDoPedido = produtoPedidoService.buscarProdutoPedidoPorIdPedido(idPedido);
        BigDecimal lucroTotalPedido = produtosDoPedido.stream()
                .map(produtoPedido -> produtoService.calcularLucroProduto(produtoPedido.getProduto().getId())
                        .multiply( BigDecimal.valueOf(produtoPedido.getQuantidade()) )
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return lucroTotalPedido;
    }

    @Override
    public void validarPedido(Integer id) {
        if (!pedidoRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("Pedido não encontrado para o Id informado.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal valorTotalDoPedido(Integer idPedido){
        validarPedido(idPedido);
        BigDecimal valorTotal = produtoPedidoService.buscarProdutoPedidoPorIdPedido(idPedido).stream()
                .map(produtoPedido -> produtoPedido.getValorTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return valorTotal;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Produto> buscarProdutosDoPedido(Integer idPedido){
        return produtoPedidoService.buscarProdutoPedidoPorIdPedido(idPedido).stream().map(produtoPedido ->
                produtoPedido.getProduto()
        ).collect(Collectors.toList());
    }
}