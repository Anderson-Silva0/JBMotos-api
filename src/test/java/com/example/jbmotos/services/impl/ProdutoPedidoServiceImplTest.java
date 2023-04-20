package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.ProdutoPedidoDTO;
import com.example.jbmotos.model.entity.Estoque;
import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.model.entity.Produto;
import com.example.jbmotos.model.entity.ProdutoPedido;
import com.example.jbmotos.model.repositories.ProdutoPedidoRepository;
import com.example.jbmotos.services.EstoqueService;
import com.example.jbmotos.services.PedidoService;
import com.example.jbmotos.services.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ProdutoPedidoServiceImplTest {

    @Autowired
    private ProdutoPedidoServiceImpl produtoPedidoService;

    @MockBean
    private PedidoService pedidoService;

    @MockBean
    private ProdutoService produtoService;

    @MockBean
    private EstoqueService estoqueService;

    @MockBean
    private ProdutoPedidoRepository produtoPedidoRepository;

    @MockBean
    private ModelMapper mapper;

    private ProdutoPedido produtoPedido;
    private ProdutoPedidoDTO produtoPedidoDTO;
    private Produto produto;
    private Pedido pedido;
    private Estoque estoque;

    @BeforeEach
    void setUp() {
        produtoPedido = getProdutoPedido();
        produtoPedidoDTO = getProdutoPedidoDTO();
        produto = ProdutoServiceImplTest.getProduto();
        pedido = PedidoServiceImplTest.getPedido();
        estoque = EstoqueServiceImplTest.getEstoque();
    }

    @Test
    @DisplayName("Deve salvar um produto referente a um pedido com sucesso")
    void salvarProdutoPedido() {
        produtoPedido.setProduto(produto);
        produtoPedido.setPedido(pedido);

        when(mapper.map(produtoPedidoDTO, ProdutoPedido.class)).thenReturn(produtoPedido);
        when(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido())).thenReturn(Optional.of(pedido));
        when(produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto())).thenReturn(Optional.of(produto));
    }

    @Test
    void buscarTodosProdutoPedido() {
    }

    @Test
    void buscarProdutoPedidoPorId() {
    }

    @Test
    void atualizarProdutoPedido() {
    }

    @Test
    void deletarProdutoPedidoPorId() {
    }

    @Test
    void buscarProdutoPedidoPorIdPedido() {
    }

    @Test
    void validarProdutoPedido() {
    }

    public static ProdutoPedido getProdutoPedido() {
        return ProdutoPedido.builder()
                .id(1)
                .pedido(null)
                .produto(null)
                .quantidade(2)
                .valorUnidade(BigDecimal.valueOf(100.00))
                .valorTotal(null)
                .build();
    }

    public static ProdutoPedidoDTO getProdutoPedidoDTO() {
        return ProdutoPedidoDTO.builder()
                .id(1)
                .idPedido(1)
                .idProduto(1)
                .quantidade(2)
                .valorUnidade(BigDecimal.valueOf(100.00))
                .valorTotal(null)
                .build();
    }
}