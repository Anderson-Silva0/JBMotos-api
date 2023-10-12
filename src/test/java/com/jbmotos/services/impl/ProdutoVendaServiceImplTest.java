package com.jbmotos.services.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.jbmotos.api.dto.ProdutoVendaDTO;
import com.jbmotos.model.entity.Estoque;
import com.jbmotos.model.entity.Produto;
import com.jbmotos.model.entity.ProdutoVenda;
import com.jbmotos.model.entity.Venda;
import com.jbmotos.model.repositories.ProdutoVendaRepository;
import com.jbmotos.services.EstoqueService;
import com.jbmotos.services.ProdutoService;
import com.jbmotos.services.VendaService;
import com.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.jbmotos.services.exception.RegraDeNegocioException;

@SpringBootTest
class ProdutoVendaServiceImplTest {

    @Autowired
    private ProdutoVendaServiceImpl produtoVendaService;

    @MockBean
    private VendaService vendaService;

    @MockBean
    private ProdutoService produtoService;

    @MockBean
    private EstoqueService estoqueService;

    @MockBean
    private ProdutoVendaRepository produtoVendaRepository;

    @MockBean
    private ModelMapper mapper;

    private ProdutoVenda produtoVenda;
    private ProdutoVendaDTO produtoVendaDTO;
    private Produto produto;
    private Venda venda;
    private Estoque estoque;

    @BeforeEach
    void setUp() {
        produtoVenda = getProdutoVenda();
        produtoVendaDTO = getProdutoVendaDTO();
        estoque = EstoqueServiceImplTest.getEstoque();
        produto = ProdutoServiceImplTest.getProduto();
        venda = VendaServiceImplTest.getVenda();
    }

    @Test
    @DisplayName("Deve salvar um produto referente a uma Venda com sucesso")
    void salvarProdutoVenda() {
        //Cenário
        estoque.setQuantidade(9);
        produto.setEstoque(estoque);
        int qtdEstoqueAntesDeSalvar = estoque.getQuantidade();

        when(mapper.map(produtoVendaDTO, ProdutoVenda.class)).thenReturn(produtoVenda);
        when(vendaService.buscarVendaPorId(produtoVendaDTO.getIdVenda())).thenReturn(venda);
        when(produtoService.buscarProdutoPorId(produtoVendaDTO.getIdProduto())).thenReturn(produto);
        when(produtoVendaRepository
                .existsProdutoVendasByVendaIdAndProdutoId(venda.getId(), produto.getId())).thenReturn(false);
        when(produtoVendaRepository.save(produtoVenda)).thenReturn(produtoVenda);

        //Execução
        ProdutoVenda produtoVendaSalvo = produtoVendaService.salvarProdutoVenda(produtoVendaDTO);

        //Verificação
        assertNotNull(produtoVendaSalvo);
        assertNotNull(produtoVendaSalvo.getVenda());
        assertNotNull(produtoVendaSalvo.getProduto());
        assertNotNull(produtoVendaSalvo.getValorUnidade());
        assertNotNull(produtoVendaSalvo.getValorTotal());
        assertEquals(produtoVenda.getProduto().getPrecoVenda(), produtoVendaSalvo.getValorUnidade());
        BigDecimal valorTotalTest = produtoVendaSalvo.getValorUnidade()
                .multiply(BigDecimal.valueOf(produtoVenda.getQuantidade()));
        assertEquals(valorTotalTest, produtoVendaSalvo.getValorTotal());
        assertEquals(qtdEstoqueAntesDeSalvar - produtoVenda.getQuantidade(), estoque.getQuantidade());

        verify(estoqueService, times(1)).atualizarEstoque(any());
        verify(produtoVendaRepository, times(1)).save(produtoVenda);
    }

    @Test
    @DisplayName("Deve lancar erro quando a Venda informada não existir")
    void erroSalvarProdutoVendaComVenda() {
        //Cenário
        when(vendaService.buscarVendaPorId(produtoVendaDTO.getIdVenda()))
                .thenThrow(new ObjetoNaoEncontradoException("Venda não encontrada para o Id informado."));

        //Execução e verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            produtoVendaService.salvarProdutoVenda(produtoVendaDTO);
        });

        assertEquals("Venda não encontrada para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro quando o produto informado não existir")
    void erroSalvarProdutoVendaComProduto() {
        //Cenário
        when(mapper.map(produtoVendaDTO, ProdutoVenda.class)).thenReturn(produtoVenda);
        when(vendaService.buscarVendaPorId(produtoVendaDTO.getIdVenda())).thenReturn(venda);
        when(produtoService.buscarProdutoPorId(produtoVendaDTO.getIdProduto()))
                .thenThrow(new ObjetoNaoEncontradoException("Produto não encontrado para o Id informado."));

        //Execução e verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            produtoVendaService.salvarProdutoVenda(produtoVendaDTO);
        });

        assertEquals("Produto não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro quando a quantidade produto for maior do que a quantidade disponível em estoque")
    void erroSalvarProdutoVendaEstoque() {
        //Cenário
        produtoVenda.setQuantidade(11);
        estoque.setQuantidade(10);
        produto.setEstoque(estoque);

        when(mapper.map(produtoVendaDTO, ProdutoVenda.class)).thenReturn(produtoVenda);
        when(vendaService.buscarVendaPorId(produtoVendaDTO.getIdVenda())).thenReturn(venda);
        when(produtoService.buscarProdutoPorId(produtoVendaDTO.getIdProduto())).thenReturn(produto);

        //Execução e verificação
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            produtoVendaService.salvarProdutoVenda(produtoVendaDTO);
        });

        assertEquals("Não é possível realizar a Venda pois a quantidade solicitada do produto é maior " +
                "do que a quantidade disponível em estoque.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro quando o produto ja estiver sido adicionado à Venda")
    void erroSalvarProdutoVendaProdutoJaAdicionadoAoVenda() {
        //Cenário
        produto.setEstoque(estoque);

        when(mapper.map(produtoVendaDTO, ProdutoVenda.class)).thenReturn(produtoVenda);
        when(vendaService.buscarVendaPorId(produtoVendaDTO.getIdVenda())).thenReturn(venda);
        when(produtoService.buscarProdutoPorId(produtoVendaDTO.getIdProduto())).thenReturn(produto);
        when(produtoVendaRepository.existsProdutoVendasByVendaIdAndProdutoId(venda.getId(), produto.getId()))
                .thenReturn(true);

        //Execução e verificação
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            produtoVendaService.salvarProdutoVenda(produtoVendaDTO);
        });

        assertEquals("Erro ao tentar Salvar, Produto já adicionado à Venda.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar uma lista de ProdutoVenda")
    void buscarTodosProdutoVenda() {
        //Cenário
        List<ProdutoVenda> listaProdutoVenda = new ArrayList<>();
        listaProdutoVenda.add(produtoVenda);
        listaProdutoVenda.add(produtoVenda);
        listaProdutoVenda.add(produtoVenda);

        when(produtoVendaRepository.findAll()).thenReturn(listaProdutoVenda);

        //Execução
        List<ProdutoVenda> produtosVendasRetornados = produtoVendaService.buscarTodosProdutoVenda();

        //Verificação
        assertNotNull(produtosVendasRetornados);
        assertEquals(3, produtosVendasRetornados.size());
        assertEquals(listaProdutoVenda, produtosVendasRetornados);
        assertEquals(ArrayList.class, produtosVendasRetornados.getClass());
    }

    @Test
    @DisplayName("Deve buscar um ProdutoVenda por id com sucesso")
    void buscarProdutoVendaPorId() {
        //Cenário
        when(produtoVendaRepository.existsById(anyInt())).thenReturn(true);
        when(produtoVendaRepository.findById(anyInt())).thenReturn(Optional.of(produtoVenda));

        //Execução
        ProdutoVenda produtoVendaBuscado = produtoVendaService.buscarProdutoVendaPorId(anyInt());

        //Verificação
        assertNotNull(produtoVendaBuscado);
        assertEquals(produtoVenda, produtoVendaBuscado);
        assertEquals(ProdutoVenda.class, produtoVendaBuscado.getClass());
    }

    @Test
    @DisplayName("Deve atualizar um ProdutoVenda com sucesso, com o mesmo produto anterior")
    void atualizarProdutoVenda() {
        //Cenário
        estoque.setQuantidade(10);
        int qtdEstoqueAntigo = estoque.getQuantidade();
        int qtdAnteriorProduto = produtoVenda.getQuantidade();
        int novaQtdProduto = produtoVendaDTO.getQuantidade();
        produto.setEstoque(estoque);
        produtoVenda.setProduto(produto);

        when(produtoVendaRepository.existsById(produtoVendaDTO.getId())).thenReturn(true);
        when(produtoVendaRepository.findById(produtoVendaDTO.getId()))
                .thenReturn(Optional.of(produtoVenda));
        when(vendaService.buscarVendaPorId(produtoVendaDTO.getIdVenda())).thenReturn(venda);
        when(produtoService.buscarProdutoPorId(produtoVendaDTO.getIdProduto())).thenReturn(produto);
        when(produtoVendaRepository.save(produtoVenda)).thenReturn(produtoVenda);

        //Execução
        ProdutoVenda produtoVendaAtualizado = produtoVendaService.atualizarProdutoVenda(produtoVendaDTO);

        //Verificação
        assertNotNull(produtoVendaAtualizado);
        assertEquals(venda, produtoVendaAtualizado.getVenda());
        assertEquals(produto, produtoVendaAtualizado.getProduto());
        assertEquals(estoque, produtoVendaAtualizado.getProduto().getEstoque());
        int qtdEstoqueAtualizado = produtoVendaAtualizado.getProduto().getEstoque().getQuantidade();
        assertEquals(qtdEstoqueAntigo + qtdAnteriorProduto - novaQtdProduto, qtdEstoqueAtualizado);
        assertEquals(produtoVenda.getProduto().getPrecoVenda(), produtoVenda.getValorUnidade());
        BigDecimal valorTotal = produtoVenda.getValorUnidade().multiply(
                BigDecimal.valueOf(produtoVendaDTO.getQuantidade())
        );
        assertEquals(valorTotal, produtoVendaAtualizado.getValorTotal());
        verify(estoqueService, times(1)).atualizarEstoque(any());
        verify(produtoVendaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar atualizar ProdutoVenda com estoque indisponivel, com o mesmo produto")
    void atualizarProdutoVendaComErroNaValidacaoDoEstoque() {
        //Cenário
        estoque.setQuantidade(10);
        produtoVendaDTO.setQuantidade(15);
        produto.setEstoque(estoque);
        produtoVenda.setProduto(produto);

        when(produtoVendaRepository.existsById(produtoVendaDTO.getId())).thenReturn(true);
        when(produtoVendaRepository.findById(produtoVendaDTO.getId()))
                .thenReturn(Optional.of(produtoVenda));
        when(vendaService.buscarVendaPorId(produtoVendaDTO.getIdVenda())).thenReturn(venda);
        when(produtoService.buscarProdutoPorId(produtoVendaDTO.getIdProduto())).thenReturn(produto);

        //Execução e verificação
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            produtoVendaService.atualizarProdutoVenda(produtoVendaDTO);
        });
        assertEquals("Não é possível Atualizar a Venda pois a quantidade solicitada do Produto" +
                " é maior do que a quantidade disponível em estoque.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar um ProdutoVenda com sucesso, com produto diferente do anterior")
    void atualizarProdutoVendaComOutroProduto() {
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
        produtoVenda.setProduto(produto);
        Integer qtdProdutoVendaAntigo = produtoVenda.getQuantidade();
        Integer qtdEstoqueAntigo = produtoVenda.getProduto().getEstoque().getQuantidade();
        Integer qtdEstoqueNovoProdutoAntes = estoqueNovoProduto.getQuantidade();

        when(produtoVendaRepository.existsById(produtoVendaDTO.getId())).thenReturn(true);
        when(produtoVendaRepository.findById(produtoVendaDTO.getId()))
                .thenReturn(Optional.of(produtoVenda));
        when(vendaService.buscarVendaPorId(produtoVendaDTO.getIdVenda())).thenReturn(venda);
        when(produtoService.buscarProdutoPorId(produtoVendaDTO.getIdProduto())).thenReturn(novoProduto);
        when(produtoVendaRepository.save(produtoVenda)).thenReturn(produtoVenda);

        //Execução
        ProdutoVenda produtoVendaAtualizado = produtoVendaService.atualizarProdutoVenda(produtoVendaDTO);

        //Verificação
        assertNotNull(produtoVendaAtualizado);
        assertEquals(qtdProdutoVendaAntigo + qtdEstoqueAntigo, estoque.getQuantidade());

        Produto novoProdutoAtualizado = produtoVendaAtualizado.getProduto();
        Estoque estoqueNovoProdutoAtualizado = novoProdutoAtualizado.getEstoque();

        assertEquals(qtdEstoqueNovoProdutoAntes - produtoVendaDTO.getQuantidade(),
                estoqueNovoProdutoAtualizado.getQuantidade());
        assertEquals(venda, produtoVendaAtualizado.getVenda());
        assertEquals(novoProduto, produtoVendaAtualizado.getProduto());
        assertEquals(produtoVendaDTO.getQuantidade(), produtoVendaAtualizado.getQuantidade());
        assertEquals(novoProduto.getPrecoVenda(), produtoVendaAtualizado.getValorUnidade());
        BigDecimal valorTotal = produtoVendaAtualizado.getValorUnidade().multiply(
                BigDecimal.valueOf(produtoVendaDTO.getQuantidade())
        );
        assertEquals(valorTotal, produtoVendaAtualizado.getValorTotal());

        verify(estoqueService, times(2)).atualizarEstoque(any());
        verify(produtoVendaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lancar erro quando atualizar o ProdutoVenda trocando o " +
            "produto com quantidade indisponível do novo produto no estoque")
    void atualizarProdutoVendaComOutroProdutoEQtdIndisponivel() {
        //Cenário
        Produto novoProduto = Produto.builder()
                .id(15)
                .nome("Capacete TAM-55")
                .precoCusto(BigDecimal.valueOf(55.50))
                .precoVenda(BigDecimal.valueOf(130.80))
                .marca("Samarino")
                .build();
        novoProduto.setEstoque(estoque);

        produtoVendaDTO.setQuantidade(1500);
        produto.setId(7);
        produto.setEstoque(estoque);
        produtoVenda.setProduto(produto);

        when(produtoVendaRepository.existsById(produtoVendaDTO.getId())).thenReturn(true);
        when(produtoVendaRepository.findById(produtoVendaDTO.getId()))
                .thenReturn(Optional.of(produtoVenda));
        when(vendaService.buscarVendaPorId(produtoVendaDTO.getIdVenda())).thenReturn(venda);
        when(produtoService.buscarProdutoPorId(produtoVendaDTO.getIdProduto())).thenReturn(novoProduto);
        when(produtoVendaRepository.save(produtoVenda)).thenReturn(produtoVenda);

        //Execução e verificação
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            produtoVendaService.atualizarProdutoVenda(produtoVendaDTO);
        });
        assertEquals("Não é possível Atualizar a Venda pois a quantidade solicitada " +
                "do novo Produto é maior do que a quantidade disponível em estoque.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao validar a existencia de um ProdutoVenda")
    void atualizarProdutoVendaNaoExistente() {
        //Cenário
        when(produtoVendaRepository.existsById(produtoVendaDTO.getId())).thenReturn(false);

        //Execução e verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            produtoVendaService.atualizarProdutoVenda(produtoVendaDTO);
        });
        assertEquals("Produto da Venda não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro quando o produto já estiver cadastrado na Venda")
    void atualizarProdutoVendaJaCadastradoNaVenda() {
        //Cenário
        produtoVenda.setProduto(produto);
        produtoVenda.setVenda(venda);

        List<ProdutoVenda> listaProdutoVenda = new ArrayList<>();
        listaProdutoVenda.add(produtoVenda);
        listaProdutoVenda.add(produtoVenda);

        when(produtoVendaRepository.existsById(produtoVendaDTO.getId())).thenReturn(true);
        when(produtoVendaRepository.findByIdNot(produtoVenda.getId())).thenReturn(listaProdutoVenda);
        when(produtoVendaRepository.findById(produtoVenda.getId())).thenReturn(Optional.of(produtoVenda));

        //Execução e verificação
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            produtoVendaService.atualizarProdutoVenda(produtoVendaDTO);
        });
        assertEquals("Erro ao tentar Atualizar, Produto já adicionado à Venda.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar um ProdutoVenda por id com sucesso")
    void deletarProdutoVendaPorId() {
        //Cenário
        produtoVenda.setProduto(produto);
        when(produtoVendaRepository.existsById(anyInt())).thenReturn(true);
        when(produtoVendaRepository.findById(anyInt())).thenReturn(Optional.of(produtoVenda));
        doNothing().when(produtoVendaRepository).deleteById(anyInt());

        //Execução
        produtoVendaService.deletarProdutoVendaPorId(anyInt());

        //Verificação
        verify(estoqueService, times(1)).adicionarQuantidadeAoEstoque(anyInt(), anyInt());
        verify(produtoVendaRepository, times(1)).deleteById(anyInt());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um ProdutoVenda por id")
    void deletarProdutoVendaPorIdComErro() {
        //Cenário
        produtoVenda.setProduto(produto);
        when(produtoVendaRepository.existsById(anyInt())).thenReturn(false);

        //Execução e verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            produtoVendaService.deletarProdutoVendaPorId(anyInt());
        });
        assertEquals("Produto da Venda não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar uma lista de ProdutoVenda por id da Venda")
    void buscarProdutoVendaPorIdVenda() {
        //Cenário
        Integer idVenda = 1;
        List<ProdutoVenda> listaProdutoVenda = new ArrayList<>();
        listaProdutoVenda.add(produtoVenda);
        listaProdutoVenda.add(produtoVenda);
        listaProdutoVenda.add(produtoVenda);

        when(produtoVendaRepository.findProdutoVendaByVendaId(idVenda))
                .thenReturn(listaProdutoVenda);

        //Execução
        List<ProdutoVenda> listaProdutoVendaRetornada = produtoVendaService
                .buscarProdutoVendaPorIdVenda(idVenda);

        //Verificação
        assertNotNull(listaProdutoVendaRetornada);
        assertEquals(3, listaProdutoVendaRetornada.size());
        assertEquals(ArrayList.class, listaProdutoVendaRetornada.getClass());
    }

    @Test
    @DisplayName("Deve lancar erro porque o ProdutoVenda por id nao existe")
    void validarProdutoVendaQuandoIdNaoExiste() {
        //Cenário
        Integer id = 1;
        when(produtoVendaRepository.existsById(id)).thenReturn(false);

        //Execução e verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            produtoVendaService.validarProdutoVenda(id);
        });
        assertEquals("Produto da Venda não encontrado para o Id informado.", exception.getMessage());
        verify(produtoVendaRepository, times(1)).existsById(id);
    }

    @Test
    @DisplayName("Nao deve lancar erro porque o ProdutoVenda por id existe")
    void validarProdutoVendaQuandoIdExiste() {
        //Cenário
        Integer id = 1;
        when(produtoVendaRepository.existsById(id)).thenReturn(true);

        //Execução e verificação
        assertDoesNotThrow(
                () -> produtoVendaService.validarProdutoVenda(id)
        );
        verify(produtoVendaRepository, times(1)).existsById(id);
    }

    public static ProdutoVenda getProdutoVenda() {
        return ProdutoVenda.builder()
                .id(1)
                .venda(null)
                .produto(null)
                .quantidade(3)
                .valorUnidade(null)
                .valorTotal(null)
                .build();
    }

    public static ProdutoVendaDTO getProdutoVendaDTO() {
        return ProdutoVendaDTO.builder()
                .id(1)
                .idVenda(1)
                .idProduto(1)
                .quantidade(5)
                .valorUnidade(null)
                .valorTotal(null)
                .build();
    }
}