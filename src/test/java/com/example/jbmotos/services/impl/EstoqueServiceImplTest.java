package com.example.jbmotos.services.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
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

import com.example.jbmotos.api.dto.EstoqueDTO;
import com.example.jbmotos.model.entity.Estoque;
import com.example.jbmotos.model.entity.Produto;
import com.example.jbmotos.model.enums.StatusEstoque;
import com.example.jbmotos.model.repositories.EstoqueRepository;
import com.example.jbmotos.services.ProdutoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;

@SpringBootTest
@ActiveProfiles("test")
class EstoqueServiceImplTest {

    @Autowired
    private EstoqueServiceImpl estoqueService;

    @MockBean
    private EstoqueRepository estoqueRepository;

    @MockBean
    private ProdutoService produtoService;

    @MockBean
    private ModelMapper mapper;

    private Estoque estoque;
    private EstoqueDTO estoqueDTO;
    private Produto produto;

    @BeforeEach
    void setUp() {
        this.estoque = getEstoque();
        this.estoqueDTO = getEstoqueDTO();
        this.produto = ProdutoServiceImplTest.getProduto();
    }

    @Test
    @DisplayName("Deve salvar um Estoque com sucesso")
    void salvarEstoque() {
        // Cenário
        when(mapper.map(estoqueDTO, Estoque.class)).thenReturn(estoque);
        when(estoqueRepository.save(any(Estoque.class))).thenReturn(estoque);

        // Execução
        Estoque estoqueSalvo = estoqueService.salvarEstoque(estoqueDTO);

        // Verificação
        assertNotNull(estoqueSalvo);
        assertEquals(StatusEstoque.DISPONIVEL, estoqueSalvo.getStatus());
        assertEquals(estoque, estoqueSalvo);
        verify(mapper, times(1)).map(estoqueDTO, Estoque.class);
        verify(estoqueRepository, times(1)).save(estoque);
    }

    @Test
    @DisplayName("Deve buscar todos os Estoques")
    void buscarTodosEstoques() {
        // Cenário
        List<Estoque> listaEstoques = new ArrayList<>();
        listaEstoques.add(estoque);
        listaEstoques.add(estoque);
        listaEstoques.add(estoque);

        when(estoqueRepository.findAll()).thenReturn(listaEstoques);

        // Execução
        List<Estoque> estoquesRetornados = estoqueService.buscarTodosEstoques();

        // Verificação
        assertNotNull(estoquesRetornados);
        assertEquals(3, estoquesRetornados.size());
        assertEquals(listaEstoques, estoquesRetornados);
        verify(estoqueRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar um Estoque por ID existente")
    void buscarEstoquePorId() {
        // Cenário
        Integer idEstoque = 1;
        when(estoqueRepository.existsById(idEstoque)).thenReturn(true);
        when(estoqueRepository.findById(idEstoque)).thenReturn(Optional.of(estoque));

        // Execução
        Optional<Estoque> estoqueOptional = estoqueService.buscarEstoquePorId(idEstoque);

        // Verificação
        assertTrue(estoqueOptional.isPresent());
        assertEquals(estoque, estoqueOptional.get());
        assertEquals(Optional.class, estoqueOptional.getClass());
        verify(estoqueRepository, times(1)).existsById(idEstoque);
        verify(estoqueRepository, times(1)).findById(idEstoque);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao buscar um Estoque por ID inexistente")
    void buscarEstoquePorIdInexistente() {
        // Cenário
        Integer idEstoque = 1;
        when(estoqueRepository.existsById(idEstoque)).thenReturn(false);

        // Execução e Verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            estoqueService.buscarEstoquePorId(idEstoque);
        });
        assertEquals("Estoque não encontrado para o Id informado.", exception.getMessage());
        verify(estoqueRepository, times(1)).existsById(idEstoque);
        verify(estoqueRepository, never()).findById(idEstoque);
    }

    @Test
    @DisplayName("Deve atualizar um Estoque com sucesso")
    void atualizarEstoque() {
        // Cenário
        when(estoqueRepository.existsById(estoqueDTO.getId())).thenReturn(true);
        when(mapper.map(estoqueDTO, Estoque.class)).thenReturn(estoque);
        estoque.setQuantidade(11);
        when(estoqueRepository.save(any(Estoque.class))).thenReturn(estoque);

        // Execução
        Estoque estoqueAtualizado = estoqueService.atualizarEstoque(estoqueDTO);

        // Verificação
        assertNotNull(estoqueAtualizado);
        assertEquals(StatusEstoque.ESTOQUE_ALTO, estoqueAtualizado.getStatus());
        assertEquals(estoque.getId(), estoqueAtualizado.getId());
        verify(estoqueRepository, times(1)).existsById(estoqueDTO.getId());
        verify(mapper, times(1)).map(estoqueDTO, Estoque.class);
        verify(estoqueRepository, times(1)).save(estoque);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar atualizar um Estoque inexistente")
    void erroAtualizarEstoque() {
        // Cenário
        when(estoqueRepository.existsById(estoqueDTO.getId())).thenReturn(false);

        // Execução e Verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            estoqueService.atualizarEstoque(estoqueDTO);
        });
        assertEquals("Estoque não encontrado para o Id informado.", exception.getMessage());
        verify(estoqueRepository, times(1)).existsById(estoqueDTO.getId());
        verify(estoqueRepository, never()).save(any(Estoque.class));
    }

    @Test
    @DisplayName("Deve deletar um Estoque por ID existente")
    void deletarEstoquePorIdExistente() {
        // Cenário
        Integer idEstoque = 1;
        when(estoqueRepository.existsById(idEstoque)).thenReturn(true);

        // Execução
        estoqueService.deletarEstoquePorId(idEstoque);

        // Verificação
        verify(estoqueRepository, times(1)).existsById(idEstoque);
        verify(estoqueRepository, times(1)).deleteById(idEstoque);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar deletar um Estoque por ID inexistente")
    void deletarEstoquePorIdInexistente() {
        // Cenário
        Integer idEstoque = 1;
        when(estoqueRepository.existsById(idEstoque)).thenReturn(false);

        // Execução e Verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            estoqueService.deletarEstoquePorId(idEstoque);
        });
        assertEquals("Estoque não encontrado para o Id informado.", exception.getMessage());
        verify(estoqueRepository, times(1)).existsById(idEstoque);
        verify(estoqueRepository, never()).deleteById(idEstoque);
    }

    @Test
    @DisplayName("Deve obter a quantidade do produto no Estoque")
    void obterQuantidadeDoProduto() {
        // Cenário
        int quantidadeEsperada = 10;
        produto.setEstoque(estoque);
        produto.getEstoque().setQuantidade(quantidadeEsperada);
        when(produtoService.buscarProdutoPorId(produto.getId()))
                .thenReturn(Optional.of(produto));

        // Execução
        Integer quantidadeObtida = estoqueService.obterQuantidadeDoProduto(produto.getId());

        // Verificação
        assertEquals(quantidadeEsperada, quantidadeObtida);
        verify(produtoService, times(1)).buscarProdutoPorId(produto.getId());
    }


    @Test
    @DisplayName("Deve adicionar quantidade ao Estoque")
    void adicionarQuantidadeAoEstoque() {
        // Cenário
        Integer idProduto = 1;
        Integer quantidadeAdicional = 10;
        Integer quantidadeAtual = 10;

        produto.setEstoque(estoque);
        produto.getEstoque().setQuantidade(quantidadeAtual);
        when(produtoService.buscarProdutoPorId(idProduto)).thenReturn(Optional.of(produto));
        when(mapper.map(estoque, EstoqueDTO.class)).thenReturn(estoqueDTO);
        when(mapper.map(estoqueDTO, Estoque.class)).thenReturn(estoque);
        when(estoqueRepository.existsById(anyInt())).thenReturn(true);

        // Execução
        estoqueService.adicionarQuantidadeAoEstoque(idProduto, quantidadeAdicional);

        // Verificação
        assertEquals(quantidadeAtual + quantidadeAdicional, estoque.getQuantidade());
        verify(produtoService, times(1)).buscarProdutoPorId(idProduto);
        verify(mapper, times(1)).map(estoque, EstoqueDTO.class);
        verify(estoqueRepository, times(1)).save(estoque);
        verify(estoqueRepository, times(1)).existsById(estoque.getId());
    }

    @Test
    @DisplayName("Deve calcular o valor total do Estoque")
    void calcularValorTotalEstoque() {
        // Cenário
        BigDecimal precoVenda = BigDecimal.valueOf(150.55);
        estoque.setQuantidade(5);
        produto.setPrecoVenda(precoVenda);
        estoque.setProduto(produto);

        List<Estoque> listaEstoque = new ArrayList<>();
        listaEstoque.add(estoque);
        listaEstoque.add(estoque);
        listaEstoque.add(estoque);

        when(estoqueRepository.findAll()).thenReturn(listaEstoque);

        // Execução
        BigDecimal valorTotalEstoque = estoqueService.calcularValorTotalEstoque();

        // Verificação
        BigDecimal valorTotalEsperado = listaEstoque.stream().map(estoqueTeste ->
                estoqueTeste.getProduto().getPrecoVenda().multiply(
                        BigDecimal.valueOf( estoqueTeste.getQuantidade() )
                )).reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(valorTotalEsperado, valorTotalEstoque);
        verify(estoqueRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao validar um Estoque inexistente")
    void validarEstoqueInexistente() {
        // Cenário
        Integer idEstoque = 1;
        when(estoqueRepository.existsById(idEstoque)).thenReturn(false);

        // Execução e Verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            estoqueService.validarEstoque(idEstoque);
        });
        assertEquals("Estoque não encontrado para o Id informado.", exception.getMessage());
        verify(estoqueRepository, times(1)).existsById(idEstoque);
    }

    @Test
    @DisplayName("Deve validar um Estoque existente")
    void validarEstoqueExistente() {
        // Cenário
        Integer idEstoque = 1;
        when(estoqueRepository.existsById(idEstoque)).thenReturn(true);

        // Execução e verificação
        assertDoesNotThrow(() -> estoqueService.validarEstoque(idEstoque) );
        verify(estoqueRepository, times(1)).existsById(idEstoque);
    }

    @Test
    @DisplayName("Deve lançar RegraDeNegocioException ao tentar verificar uso de Estoque vinculado a um Produto")
    void verificarUsoEstoqueComProdutoVinculado() {
        // Cenário
        Integer id = 1;
        when(produtoService.existeProdutoPorIdEstoque(id)).thenReturn(true);

        // Execução e Verificação
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            estoqueService.verificarUsoEstoque(id);
        });
        assertEquals("O Estoque pertence a um Produto.", exception.getMessage());
        verify(produtoService, times(1)).existeProdutoPorIdEstoque(id);
    }


    public static Estoque getEstoque() {
        return Estoque.builder()
                .id(1)
                .produto(null)
                .estoqueMinimo(5)
                .estoqueMaximo(10)
                .quantidade(5)
                .status(null)
                .build();
    }

    public static EstoqueDTO getEstoqueDTO() {
        return EstoqueDTO.builder()
                .id(1)
                .estoqueMinimo(5)
                .estoqueMaximo(10)
                .quantidade(5)
                .status(null)
                .build();
    }
}