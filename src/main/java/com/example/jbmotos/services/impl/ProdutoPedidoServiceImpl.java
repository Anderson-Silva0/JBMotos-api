package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.ProdutoPedidoDTO;
import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.model.entity.ProdutoPedido;
import com.example.jbmotos.model.repositories.ProdutoPedidoRepository;
import com.example.jbmotos.services.PedidoService;
import com.example.jbmotos.services.ProdutoPedidoService;
import com.example.jbmotos.services.ProdutoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public ProdutoPedido salvarProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO) {
        ProdutoPedido produtoPedido = mapper.map(produtoPedidoDTO, ProdutoPedido.class);
        produtoPedido.setPedido(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido()).get());
        produtoPedido.setProduto(produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto()).get());
        produtoPedido.setValorUnidade(produtoPedido.getProduto().getPrecoVenda());
        produtoPedido.setValorTotal(
                produtoPedido.getValorUnidade().multiply(
                        BigDecimal.valueOf( produtoPedido.getQuantidade() )
                )
        );
        return produtoPedidoRepository.save(produtoPedido);
    }

    @Override
    public List<ProdutoPedido> buscarProdutosDoPedido(Integer idPedido) {
        return produtoPedidoRepository.findProdutoPedidoByPedidoId(idPedido);
    }

    @Override
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
    public Optional<ProdutoPedido> buscarProdutoPedidoPorId(Integer id) {
        return Optional.empty();
    }

    @Override
    public ProdutoPedido atualizarProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO) {
        return null;
    }

    @Override
    public void deletarProdutoPedido(Integer id) {

    }

    @Override
    public void validaProdutoPedido(Integer id) {

    }
}
