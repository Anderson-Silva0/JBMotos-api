package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.ProdutoPedidoDTO;
import com.example.jbmotos.model.entity.ProdutoPedido;
import com.example.jbmotos.model.repositories.ProdutoPedidoRepository;
import com.example.jbmotos.services.PedidoService;
import com.example.jbmotos.services.ProdutoPedidoService;
import com.example.jbmotos.services.ProdutoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoPedidoServiceImpl implements ProdutoPedidoService {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoPedidoRepository produtoPedidoRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public ProdutoPedido salvarProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO) {
        return produtoPedidoRepository.save(getProdutoPedido(produtoPedidoDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoPedido> buscarTodosProdutoPedido() {
        return produtoPedidoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProdutoPedido> buscarProdutoPedidoPorId(Integer id) {
        validaProdutoPedido(id);
        return produtoPedidoRepository.findById(id);
    }

    @Override
    @Transactional
    public ProdutoPedido atualizarProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO) {
        validaProdutoPedido(produtoPedidoDTO.getId());
        return produtoPedidoRepository.save(getProdutoPedido(produtoPedidoDTO));
    }

    @Override
    @Transactional
    public void deletarProdutoPedidoPorId(Integer id) {
        validaProdutoPedido(id);
        produtoPedidoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoPedido> buscarProdutosDoPedido(Integer idPedido) {
        return produtoPedidoRepository.findProdutoPedidoByPedidoId(idPedido);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal valorTotalDoPedido(Integer idPedido) {
        pedidoService.validaPedido(idPedido);
        BigDecimal valorTotal = BigDecimal.ZERO;
        List<BigDecimal> valoresTotais = buscarProdutosDoPedido(idPedido).stream().map(p ->
                p.getValorTotal()
        ).collect(Collectors.toList());
        for(BigDecimal valor : valoresTotais) {
            valorTotal = valorTotal.add(valor);
        }
        return valorTotal;
    }

    @Override
    public void validaProdutoPedido(Integer id) {
        if ( !produtoPedidoRepository.existsById(id) ) {
            throw new ObjetoNaoEncontradoException("Produto do Pedido não encontrado para o Id informado.");
        }
    }

    @Override
    public ProdutoPedido getProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO) {
        ProdutoPedido produtoPedido = mapper.map(produtoPedidoDTO, ProdutoPedido.class);
        produtoPedido.setPedido(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido()).get());
        produtoPedido.setProduto(produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto()).get());
        produtoPedido.setValorUnidade(produtoPedido.getProduto().getPrecoVenda());
        produtoPedido.setValorTotal(
                produtoPedido.getValorUnidade().multiply(
                        BigDecimal.valueOf(produtoPedido.getQuantidade())
                ));
        return produtoPedido;
    }
}
