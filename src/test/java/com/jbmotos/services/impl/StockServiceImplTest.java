package com.jbmotos.services.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.jbmotos.model.entity.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.jbmotos.api.dto.StockDTO;
import com.jbmotos.model.entity.Product;
import com.jbmotos.model.enums.StockStatus;
import com.jbmotos.model.repositories.StockRepository;
import com.jbmotos.services.ProductService;
import com.jbmotos.services.exception.ObjectNotFoundException;
import com.jbmotos.services.exception.BusinessRuleException;

@SpringBootTest
class StockServiceImplTest {

    @Autowired
    private StockServiceImpl estoqueService;

    @MockBean
    private StockRepository stockRepository;

    @MockBean
    private ProductService productService;

    @MockBean
    private ModelMapper mapper;

    private Stock stock;
    private StockDTO stockDTO;
    private Product product;

    @BeforeEach
    void setUp() {
        this.stock = getEstoque();
        this.stockDTO = getEstoqueDTO();
        this.product = ProductServiceImplTest.getProduto();
    }

    @Test
    @DisplayName("Deve salvar um Estoque com sucesso")
    void saveStock() {
        // Cenário
        when(mapper.map(stockDTO, Stock.class)).thenReturn(stock);
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        // Execução
        Stock stockSalvo = estoqueService.saveStock(stockDTO);

        // Verificação
        assertNotNull(stockSalvo);
        assertEquals(StockStatus.AVAILABLE, stockSalvo.getStatus());
        assertEquals(stock, stockSalvo);
        verify(mapper, times(1)).map(stockDTO, Stock.class);
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    @DisplayName("Deve buscar todos os Estoques")
    void findAllStocks() {
        // Cenário
        List<Stock> listaStocks = new ArrayList<>();
        listaStocks.add(stock);
        listaStocks.add(stock);
        listaStocks.add(stock);

        when(stockRepository.findAll()).thenReturn(listaStocks);

        // Execução
        List<Stock> estoquesRetornados = estoqueService.findAllStocks();

        // Verificação
        assertNotNull(estoquesRetornados);
        assertEquals(3, estoquesRetornados.size());
        assertEquals(listaStocks, estoquesRetornados);
        verify(stockRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar um Estoque por ID existente")
    void findStockById() {
        // Cenário
        Integer idEstoque = 1;
        when(stockRepository.existsById(idEstoque)).thenReturn(true);
        when(stockRepository.findById(idEstoque)).thenReturn(Optional.of(this.stock));

        // Execução
        Stock stock = estoqueService.findStockById(idEstoque);

        // Verificação
        assertEquals(stock, stock);
        assertEquals(Stock.class, stock.getClass());
        verify(stockRepository, times(1)).findById(idEstoque);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao buscar um Estoque por ID inexistente")
    void findStockByIdInexistente() {
        // Cenário
        Integer idEstoque = 1;
        when(stockRepository.existsById(idEstoque)).thenReturn(false);

        // Execução e Verificação
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            estoqueService.findStockById(idEstoque);
        });
        assertEquals("Estoque não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar um Estoque com sucesso")
    void updateStock() {
        // Cenário
        when(stockRepository.existsById(stockDTO.getId())).thenReturn(true);
        when(mapper.map(stockDTO, Stock.class)).thenReturn(stock);
        stock.setQuantity(11);
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        // Execução
        Stock stockAtualizado = estoqueService.updateStock(stockDTO);

        // Verificação
        assertNotNull(stockAtualizado);
        assertEquals(StockStatus.HIGH_STOCK, stockAtualizado.getStatus());
        assertEquals(stock.getId(), stockAtualizado.getId());
        verify(stockRepository, times(1)).existsById(stockDTO.getId());
        verify(mapper, times(1)).map(stockDTO, Stock.class);
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar atualizar um Estoque inexistente")
    void erroUpdateStock() {
        // Cenário
        when(stockRepository.existsById(stockDTO.getId())).thenReturn(false);

        // Execução e Verificação
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            estoqueService.updateStock(stockDTO);
        });
        assertEquals("Estoque não encontrado para o Id informado.", exception.getMessage());
        verify(stockRepository, times(1)).existsById(stockDTO.getId());
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Deve deletar um Estoque por ID existente")
    void deleteStockByIdExistente() {
        // Cenário
        Integer idEstoque = 1;
        when(stockRepository.existsById(idEstoque)).thenReturn(true);

        // Execução
        estoqueService.deleteStockById(idEstoque);

        // Verificação
        verify(stockRepository, times(1)).existsById(idEstoque);
        verify(stockRepository, times(1)).deleteById(idEstoque);
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao tentar deletar um Estoque por ID inexistente")
    void deleteStockByIdInexistente() {
        // Cenário
        Integer idEstoque = 1;
        when(stockRepository.existsById(idEstoque)).thenReturn(false);

        // Execução e Verificação
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            estoqueService.deleteStockById(idEstoque);
        });
        assertEquals("Estoque não encontrado para o Id informado.", exception.getMessage());
        verify(stockRepository, times(1)).existsById(idEstoque);
        verify(stockRepository, never()).deleteById(idEstoque);
    }

    @Test
    @DisplayName("Deve obter a quantidade do produto no Estoque")
    void getProductQuantity() {
        // Cenário
        int quantidadeEsperada = 10;
        product.setStock(stock);
        product.getStock().setQuantity(quantidadeEsperada);
        when(productService.findProductById(product.getId()))
                .thenReturn(product);

        // Execução
        Integer quantidadeObtida = estoqueService.getProductQuantity(product.getId());

        // Verificação
        assertEquals(quantidadeEsperada, quantidadeObtida);
        verify(productService, times(1)).findProductById(product.getId());
    }


    @Test
    @DisplayName("Deve adicionar quantidade ao Estoque")
    void addStockQuantity() {
        // Cenário
        Integer idProduto = 1;
        Integer quantidadeAdicional = 10;
        Integer quantidadeAtual = 10;

        product.setStock(stock);
        product.getStock().setQuantity(quantidadeAtual);
        when(productService.findProductById(idProduto)).thenReturn(product);
        when(mapper.map(stock, StockDTO.class)).thenReturn(stockDTO);
        when(mapper.map(stockDTO, Stock.class)).thenReturn(stock);
        when(stockRepository.existsById(anyInt())).thenReturn(true);

        // Execução
        estoqueService.addStockQuantity(idProduto, quantidadeAdicional);

        // Verificação
        assertEquals(quantidadeAtual + quantidadeAdicional, stock.getQuantity());
        verify(productService, times(1)).findProductById(idProduto);
        verify(mapper, times(1)).map(stock, StockDTO.class);
        verify(stockRepository, times(1)).save(stock);
        verify(stockRepository, times(1)).existsById(stock.getId());
    }

    @Test
    @DisplayName("Deve calcular o valor total de custo do Estoque")
    void calculateTotalStockCost() {
        // Cenário
        BigDecimal precoCusto = BigDecimal.valueOf(150.55);
        stock.setQuantity(5);
        product.setCostPrice(precoCusto);
        stock.setProduct(product);

        List<Stock> listaStock = new ArrayList<>();
        listaStock.add(stock);
        listaStock.add(stock);
        listaStock.add(stock);

        when(stockRepository.findAll()).thenReturn(listaStock);

        // Execução
        BigDecimal valorCustoTotalEstoque = estoqueService.calculateTotalStockCost();

        // Verificação
        BigDecimal valorTotalEsperado = listaStock.stream().map(estoqueTeste ->
                estoqueTeste.getProduct().getCostPrice().multiply(
                        BigDecimal.valueOf( estoqueTeste.getQuantity() )
                )).reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(valorTotalEsperado, valorCustoTotalEstoque);
        verify(stockRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve calcular o potencial de venda do Estoque")
    void calcularPontecialVendaEstoque() {
        // Cenário
        BigDecimal precoVenda = BigDecimal.valueOf(150.55);
        stock.setQuantity(5);
        product.setSalePrice(precoVenda);
        stock.setProduct(product);

        List<Stock> listaStock = new ArrayList<>();
        listaStock.add(stock);
        listaStock.add(stock);
        listaStock.add(stock);

        when(stockRepository.findAll()).thenReturn(listaStock);

        // Execução
        BigDecimal valorTotalEstoque = estoqueService.calculateStockSalesPotential();

        // Verificação
        BigDecimal valorTotalEsperado = listaStock.stream().map(estoqueTeste ->
                estoqueTeste.getProduct().getSalePrice().multiply(
                        BigDecimal.valueOf( estoqueTeste.getQuantity() )
                )).reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(valorTotalEsperado, valorTotalEstoque);
        verify(stockRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar ObjetoNaoEncontradoException ao validar um Estoque inexistente")
    void validateStockInexistente() {
        // Cenário
        Integer idEstoque = 1;
        when(stockRepository.existsById(idEstoque)).thenReturn(false);

        // Execução e Verificação
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            estoqueService.validateStock(idEstoque);
        });
        assertEquals("Estoque não encontrado para o Id informado.", exception.getMessage());
        verify(stockRepository, times(1)).existsById(idEstoque);
    }

    @Test
    @DisplayName("Deve validar um Estoque existente")
    void validateStockExistente() {
        // Cenário
        Integer idEstoque = 1;
        when(stockRepository.existsById(idEstoque)).thenReturn(true);

        // Execução e verificação
        assertDoesNotThrow(() -> estoqueService.validateStock(idEstoque) );
        verify(stockRepository, times(1)).existsById(idEstoque);
    }

    @Test
    @DisplayName("Deve lançar RegraDeNegocioException ao tentar verificar uso de Estoque vinculado a um Produto")
    void checkStockUsageComProdutoVinculado() {
        // Cenário
        Integer id = 1;
        when(productService.existsProductByStockId(id)).thenReturn(true);

        // Execução e Verificação
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            estoqueService.checkStockUsage(id);
        });
        assertEquals("O Estoque pertence a um Produto.", exception.getMessage());
        verify(productService, times(1)).existsProductByStockId(id);
    }


    public static Stock getEstoque() {
        return Stock.builder()
                .id(1)
                .product(null)
                .minStock(5)
                .maxStock(10)
                .quantity(5)
                .status(null)
                .build();
    }

    public static StockDTO getEstoqueDTO() {
        return StockDTO.builder()
                .id(1)
                .minStock(5)
                .maxStock(10)
                .quantity(5)
                .status(null)
                .build();
    }
}