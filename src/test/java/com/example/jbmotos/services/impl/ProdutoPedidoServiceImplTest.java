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
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
        estoque = EstoqueServiceImplTest.getEstoque();
        produto = ProdutoServiceImplTest.getProduto();
        pedido = PedidoServiceImplTest.getPedido();
    }

    @Test
    @DisplayName("Deve salvar um produto referente a um pedido com sucesso")
    void salvarProdutoPedido() {
        //Cenário
        estoque.setQuantidade(9);
        produto.setEstoque(estoque);
        int qtdEstoqueAntesDeSalvar = estoque.getQuantidade();

        when(mapper.map(produtoPedidoDTO, ProdutoPedido.class)).thenReturn(produtoPedido);
        when(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido())).thenReturn(Optional.of(pedido));
        when(produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto())).thenReturn(Optional.of(produto));
        when(produtoPedidoRepository
                .existsProdutoPedidosByPedidoIdAndProdutoId(pedido.getId(), produto.getId())).thenReturn(false);
        when(produtoPedidoRepository.save(produtoPedido)).thenReturn(produtoPedido);

        //Execução
        ProdutoPedido produtoPedidoSalvo = produtoPedidoService.salvarProdutoPedido(produtoPedidoDTO);

        //Verificação
        assertNotNull(produtoPedidoSalvo);
        assertNotNull(produtoPedidoSalvo.getPedido());
        assertNotNull(produtoPedidoSalvo.getProduto());
        assertNotNull(produtoPedidoSalvo.getValorUnidade());
        assertNotNull(produtoPedidoSalvo.getValorTotal());
        assertEquals(produtoPedido.getProduto().getPrecoVenda(), produtoPedidoSalvo.getValorUnidade());
        BigDecimal valorTotalTest = produtoPedidoSalvo.getValorUnidade()
                .multiply(BigDecimal.valueOf(produtoPedido.getQuantidade()));
        assertEquals(valorTotalTest, produtoPedidoSalvo.getValorTotal());
        assertEquals(qtdEstoqueAntesDeSalvar - produtoPedido.getQuantidade(), estoque.getQuantidade());

        verify(estoqueService, times(1)).atualizarEstoque(any());
        verify(produtoPedidoRepository, times(1)).save(produtoPedido);
    }

    @Test
    @DisplayName("Deve lancar erro quando o pedido informado não existir")
    void erroSalvarProdutoPedidoComPedido() {
        //Cenário
        when(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido()))
                .thenThrow(new ObjetoNaoEncontradoException("Pedido não encontrado para o Id informado."));

        //Execução e verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            produtoPedidoService.salvarProdutoPedido(produtoPedidoDTO);
        });

        assertEquals("Pedido não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro quando o produto informado não existir")
    void erroSalvarProdutoPedidoComProduto() {
        //Cenário
        when(mapper.map(produtoPedidoDTO, ProdutoPedido.class)).thenReturn(produtoPedido);
        when(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido())).thenReturn(Optional.of(pedido));
        when(produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto()))
                .thenThrow(new ObjetoNaoEncontradoException("Produto não encontrado para o Id informado."));

        //Execução e verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            produtoPedidoService.salvarProdutoPedido(produtoPedidoDTO);
        });

        assertEquals("Produto não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro quando a quantidade produto for maior do que a quantidade disponível em estoque")
    void erroSalvarProdutoPedidoEstoque() {
        //Cenário
        produtoPedido.setQuantidade(11);
        estoque.setQuantidade(10);
        produto.setEstoque(estoque);

        when(mapper.map(produtoPedidoDTO, ProdutoPedido.class)).thenReturn(produtoPedido);
        when(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido())).thenReturn(Optional.of(pedido));
        when(produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto())).thenReturn(Optional.of(produto));

        //Execução e verificação
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            produtoPedidoService.salvarProdutoPedido(produtoPedidoDTO);
        });

        assertEquals("Não é possível realizar o Pedido pois a quantidade solicitada do produto é maior " +
                "do que a quantidade disponível em estoque.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro quando o produto ja estiver sido adicionado no pedido")
    void erroSalvarProdutoPedidoProdutoJaAdicionadoAoPedido() {
        //Cenário
        produto.setEstoque(estoque);

        when(mapper.map(produtoPedidoDTO, ProdutoPedido.class)).thenReturn(produtoPedido);
        when(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido())).thenReturn(Optional.of(pedido));
        when(produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto())).thenReturn(Optional.of(produto));
        when(produtoPedidoRepository.existsProdutoPedidosByPedidoIdAndProdutoId(pedido.getId(), produto.getId()))
                .thenReturn(true);

        //Execução e verificação
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            produtoPedidoService.salvarProdutoPedido(produtoPedidoDTO);
        });

        assertEquals("Erro ao tentar Salvar, Produto já adicionado ao Pedido.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar uma lista de ProdutoPedido")
    void buscarTodosProdutoPedido() {
        //Cenário
        List<ProdutoPedido> listaProdutoPedido = new ArrayList<>();
        listaProdutoPedido.add(produtoPedido);
        listaProdutoPedido.add(produtoPedido);
        listaProdutoPedido.add(produtoPedido);

        when(produtoPedidoRepository.findAll()).thenReturn(listaProdutoPedido);

        //Execução
        List<ProdutoPedido> produtosPedidosRetornados = produtoPedidoService.buscarTodosProdutoPedido();

        //Verificação
        assertNotNull(produtosPedidosRetornados);
        assertEquals(3, produtosPedidosRetornados.size());
        assertEquals(listaProdutoPedido, produtosPedidosRetornados);
        assertEquals(ArrayList.class, produtosPedidosRetornados.getClass());
    }

    @Test
    @DisplayName("Deve buscar um ProdutoPedido por id com sucesso")
    void buscarProdutoPedidoPorId() {
        //Cenário
        when(produtoPedidoRepository.existsById(anyInt())).thenReturn(true);
        when(produtoPedidoRepository.findById(anyInt())).thenReturn(Optional.of(produtoPedido));

        //Execução
        Optional<ProdutoPedido> produtoPedidoOptional = produtoPedidoService.buscarProdutoPedidoPorId(anyInt());

        //Verificação
        assertNotNull(produtoPedidoOptional);
        assertTrue(produtoPedidoOptional.isPresent());
        assertEquals(produtoPedido, produtoPedidoOptional.get());
        assertEquals(Optional.class, produtoPedidoOptional.getClass());
    }

    @Test
    @DisplayName("Deve atualizar um ProdutoPedido com sucesso, com o mesmo produto anterior")
    void atualizarProdutoPedido() {
        //Cenário
        estoque.setQuantidade(10);
        int qtdEstoqueAntigo = estoque.getQuantidade();
        int qtdAnteriorProduto = produtoPedido.getQuantidade();
        int novaQtdProduto = produtoPedidoDTO.getQuantidade();
        produto.setEstoque(estoque);
        produtoPedido.setProduto(produto);

        when(produtoPedidoRepository.existsById(produtoPedidoDTO.getId())).thenReturn(true);
        when(produtoPedidoService.buscarProdutoPedidoPorId(produtoPedidoDTO.getId()))
                .thenReturn(Optional.of(produtoPedido));
        when(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido())).thenReturn(Optional.of(pedido));
        when(produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto())).thenReturn(Optional.of(produto));
        when(produtoPedidoRepository.save(produtoPedido)).thenReturn(produtoPedido);

        //Execução
        ProdutoPedido produtoPedidoAtualizado = produtoPedidoService.atualizarProdutoPedido(produtoPedidoDTO);

        //Verificação
        assertNotNull(produtoPedidoAtualizado);
        assertEquals(pedido, produtoPedidoAtualizado.getPedido());
        assertEquals(produto, produtoPedidoAtualizado.getProduto());
        assertEquals(estoque, produtoPedidoAtualizado.getProduto().getEstoque());
        int qtdEstoqueAtualizado = produtoPedidoAtualizado.getProduto().getEstoque().getQuantidade();
        assertEquals(qtdEstoqueAntigo + qtdAnteriorProduto - novaQtdProduto, qtdEstoqueAtualizado);
        assertEquals(produtoPedido.getProduto().getPrecoVenda(), produtoPedido.getValorUnidade());
        BigDecimal valorTotal = produtoPedido.getValorUnidade().multiply(
                BigDecimal.valueOf(produtoPedidoDTO.getQuantidade())
        );
        assertEquals(valorTotal, produtoPedidoAtualizado.getValorTotal());
        verify(estoqueService, times(1)).atualizarEstoque(any());
        verify(produtoPedidoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar atualizar ProdutoPedido com estoque indisponivel, com o mesmo produto")
    void atualizarProdutoPedidoComErroNaValidacaoDoEstoque() {
        //Cenário
        estoque.setQuantidade(10);
        produtoPedidoDTO.setQuantidade(15);
        produto.setEstoque(estoque);
        produtoPedido.setProduto(produto);

        when(produtoPedidoRepository.existsById(produtoPedidoDTO.getId())).thenReturn(true);
        when(produtoPedidoService.buscarProdutoPedidoPorId(produtoPedidoDTO.getId()))
                .thenReturn(Optional.of(produtoPedido));
        when(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido())).thenReturn(Optional.of(pedido));
        when(produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto())).thenReturn(Optional.of(produto));

        //Execução e verificação
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            produtoPedidoService.atualizarProdutoPedido(produtoPedidoDTO);
        });
        assertEquals("Não é possível Atualizar o Pedido pois a quantidade solicitada do Produto" +
                " é maior do que a quantidade disponível em estoque.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar um ProdutoPedido com sucesso, com produto diferente do anterior")
    void atualizarProdutoPedidoComOutroProduto() {
        //Cenário
        Estoque estoqueNovoProduto = Estoque.builder()
                .id(5)
                .estoqueMinimo(6)
                .estoqueMaximo(15)
                .quantidade(8)
                .build();
        Produto novoProduto = Produto.builder()
                .id(15)
                .nome("Capacete TAM-55")
                .precoCusto(BigDecimal.valueOf(55.50))
                .precoVenda(BigDecimal.valueOf(130.80))
                .marca("Samarino")
                .build();
        novoProduto.setEstoque(estoqueNovoProduto);

        produto.setId(7);
        produto.setEstoque(estoque);
        produtoPedido.setProduto(produto);
        Integer qtdProdutoPedidoAntigo = produtoPedido.getQuantidade();
        Integer qtdEstoqueAntigo = produtoPedido.getProduto().getEstoque().getQuantidade();
        Integer qtdEstoqueNovoProdutoAntes = estoqueNovoProduto.getQuantidade();

        when(produtoPedidoRepository.existsById(produtoPedidoDTO.getId())).thenReturn(true);
        when(produtoPedidoService.buscarProdutoPedidoPorId(produtoPedidoDTO.getId()))
                .thenReturn(Optional.of(produtoPedido));
        when(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido())).thenReturn(Optional.of(pedido));
        when(produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto())).thenReturn(Optional.of(novoProduto));
        when(produtoPedidoRepository.save(produtoPedido)).thenReturn(produtoPedido);

        //Execução
        ProdutoPedido produtoPedidoAtualizado = produtoPedidoService.atualizarProdutoPedido(produtoPedidoDTO);

        //Verificação
        assertNotNull(produtoPedidoAtualizado);
        assertEquals(qtdProdutoPedidoAntigo + qtdEstoqueAntigo, estoque.getQuantidade());

        Produto novoProdutoAtualizado = produtoPedidoAtualizado.getProduto();
        Estoque estoqueNovoProdutoAtualizado = novoProdutoAtualizado.getEstoque();

        assertEquals(qtdEstoqueNovoProdutoAntes - produtoPedidoDTO.getQuantidade(),
                estoqueNovoProdutoAtualizado.getQuantidade());
        assertEquals(pedido, produtoPedidoAtualizado.getPedido());
        assertEquals(novoProduto, produtoPedidoAtualizado.getProduto());
        assertEquals(produtoPedidoDTO.getQuantidade(), produtoPedidoAtualizado.getQuantidade());
        assertEquals(novoProduto.getPrecoVenda(), produtoPedidoAtualizado.getValorUnidade());
        BigDecimal valorTotal = produtoPedidoAtualizado.getValorUnidade().multiply(
                BigDecimal.valueOf(produtoPedidoDTO.getQuantidade())
        );
        assertEquals(valorTotal, produtoPedidoAtualizado.getValorTotal());

        verify(estoqueService, times(2)).atualizarEstoque(any());
        verify(produtoPedidoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lancar erro quando atualizar o ProdutoPedido trocando o " +
            "produto com quantidade indisponível do novo produto no estoque")
    void atualizarProdutoPedidoComOutroProdutoEQtdIndisponivel() {
        //Cenário
        Produto novoProduto = Produto.builder()
                .id(15)
                .nome("Capacete TAM-55")
                .precoCusto(BigDecimal.valueOf(55.50))
                .precoVenda(BigDecimal.valueOf(130.80))
                .marca("Samarino")
                .build();
        novoProduto.setEstoque(estoque);

        produtoPedidoDTO.setQuantidade(1500);
        produto.setId(7);
        produto.setEstoque(estoque);
        produtoPedido.setProduto(produto);

        when(produtoPedidoRepository.existsById(produtoPedidoDTO.getId())).thenReturn(true);
        when(produtoPedidoService.buscarProdutoPedidoPorId(produtoPedidoDTO.getId()))
                .thenReturn(Optional.of(produtoPedido));
        when(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido())).thenReturn(Optional.of(pedido));
        when(produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto())).thenReturn(Optional.of(novoProduto));
        when(produtoPedidoRepository.save(produtoPedido)).thenReturn(produtoPedido);

        //Execução e verificação
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            produtoPedidoService.atualizarProdutoPedido(produtoPedidoDTO);
        });
        assertEquals("Não é possível Atualizar o Pedido pois a quantidade solicitada " +
                "do novo Produto é maior do que a quantidade disponível em estoque.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao validar a existencia de um ProdutoPedido")
    void atualizarProdutoPedidoNaoExistente() {
        //Cenário
        when(produtoPedidoRepository.existsById(produtoPedidoDTO.getId())).thenReturn(false);

        //Execução e verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            produtoPedidoService.atualizarProdutoPedido(produtoPedidoDTO);
        });
        assertEquals("Produto do Pedido não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro quando o produto ja estiver cadastrado no pedido")
    void atualizarProdutoPedidoJaCadastradoNoPedido() {
        //Cenário
        produtoPedido.setProduto(produto);
        produtoPedido.setPedido(pedido);

        List<ProdutoPedido> listaProdutoPedido = new ArrayList<>();
        listaProdutoPedido.add(produtoPedido);
        listaProdutoPedido.add(produtoPedido);

        when(produtoPedidoRepository.existsById(produtoPedidoDTO.getId())).thenReturn(true);
        when(produtoPedidoService.buscarProdutoPedidoPorId(produtoPedidoDTO.getId()))
                .thenReturn(Optional.of(produtoPedido));
        when(produtoPedidoRepository.findByIdNot(produtoPedido.getId())).thenReturn(listaProdutoPedido);

        //Execução e verificação
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            produtoPedidoService.atualizarProdutoPedido(produtoPedidoDTO);
        });
        assertEquals("Erro ao tentar Atualizar, Produto já adicionado ao Pedido.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar um ProdutoPedido por id com sucesso")
    void deletarProdutoPedidoPorId() {
        //Cenário
        produtoPedido.setProduto(produto);
        when(produtoPedidoRepository.existsById(anyInt())).thenReturn(true);
        when(produtoPedidoService.buscarProdutoPedidoPorId(anyInt())).thenReturn(Optional.of(produtoPedido));
        doNothing().when(produtoPedidoRepository).deleteById(anyInt());

        //Execução
        produtoPedidoService.deletarProdutoPedidoPorId(anyInt());

        //Verificação
        verify(estoqueService, times(1)).adicionarQuantidadeAoEstoque(anyInt(), anyInt());
        verify(produtoPedidoRepository, times(1)).deleteById(anyInt());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um ProdutoPedido por id")
    void deletarProdutoPedidoPorIdComErro() {
        //Cenário
        produtoPedido.setProduto(produto);
        when(produtoPedidoRepository.existsById(anyInt())).thenReturn(false);

        //Execução e verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            produtoPedidoService.deletarProdutoPedidoPorId(anyInt());
        });
        assertEquals("Produto do Pedido não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar uma lista de ProdutoPedido por id do Pedido")
    void buscarProdutoPedidoPorIdPedido() {
        //Cenário
        Integer idPedido = 1;
        List<ProdutoPedido> listaProdutoPedido = new ArrayList<>();
        listaProdutoPedido.add(produtoPedido);
        listaProdutoPedido.add(produtoPedido);
        listaProdutoPedido.add(produtoPedido);

        when(produtoPedidoRepository.findProdutoPedidoByPedidoId(idPedido))
                .thenReturn(listaProdutoPedido);

        //Execução
        List<ProdutoPedido> listaProdutoPedidoRetornada = produtoPedidoService
                .buscarProdutoPedidoPorIdPedido(idPedido);

        //Verificação
        assertNotNull(listaProdutoPedidoRetornada);
        assertEquals(3, listaProdutoPedidoRetornada.size());
        assertEquals(ArrayList.class, listaProdutoPedidoRetornada.getClass());
    }

    @Test
    @DisplayName("Deve lancar erro porque o ProdutoPedido por id nao existe")
    void validarProdutoPedidoQuandoIdNaoExiste() {
        //Cenário
        Integer id = 1;
        when(produtoPedidoRepository.existsById(id)).thenReturn(false);

        //Execução e verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            produtoPedidoService.validarProdutoPedido(id);
        });
        assertEquals("Produto do Pedido não encontrado para o Id informado.", exception.getMessage());
        verify(produtoPedidoRepository, times(1)).existsById(id);
    }

    @Test
    @DisplayName("Nao deve lancar erro porque o ProdutoPedido por id existe")
    void validarProdutoPedidoQuandoIdExiste() {
        //Cenário
        Integer id = 1;
        when(produtoPedidoRepository.existsById(id)).thenReturn(true);

        //Execução e verificação
        assertDoesNotThrow(
                () -> produtoPedidoService.validarProdutoPedido(id)
        );
        verify(produtoPedidoRepository, times(1)).existsById(id);
    }

    public static ProdutoPedido getProdutoPedido() {
        return ProdutoPedido.builder()
                .id(1)
                .pedido(null)
                .produto(null)
                .quantidade(3)
                .valorUnidade(null)
                .valorTotal(null)
                .build();
    }

    public static ProdutoPedidoDTO getProdutoPedidoDTO() {
        return ProdutoPedidoDTO.builder()
                .id(1)
                .idPedido(1)
                .idProduto(1)
                .quantidade(5)
                .valorUnidade(null)
                .valorTotal(null)
                .build();
    }
}