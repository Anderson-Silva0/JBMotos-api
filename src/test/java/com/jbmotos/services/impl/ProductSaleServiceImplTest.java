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

import com.jbmotos.model.entity.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.jbmotos.api.dto.ProductDTO;
import com.jbmotos.api.dto.ProductsOfSaleDTO;
import com.jbmotos.model.entity.Product;
import com.jbmotos.model.entity.ProductsOfSale;
import com.jbmotos.model.entity.Sale;
import com.jbmotos.model.repositories.ProductsOfSaleRepository;
import com.jbmotos.services.StockService;
import com.jbmotos.services.ProductService;
import com.jbmotos.services.SaleService;
import com.jbmotos.services.exception.ObjectNotFoundException;
import com.jbmotos.services.exception.BusinessRuleException;

@SpringBootTest
class ProductSaleServiceImplTest {

    @Autowired
    private ProductsOfSaleServiceImpl produtoVendaService;

    @MockBean
    private SaleService saleService;

    @MockBean
    private ProductService productService;

    @MockBean
    private StockService stockService;

    @MockBean
    private ProductsOfSaleRepository productsOfSaleRepository;

    @MockBean
    private ModelMapper mapper;

    private ProductsOfSale productsOfSale;
    private ProductsOfSaleDTO productsOfSaleDTO;
    private Product product;
    private Sale sale;
    private Stock stock;

    @BeforeEach
    void setUp() {
        productsOfSale = getProdutoVenda();
        productsOfSaleDTO = getProdutoVendaDTO();
        stock = StockServiceImplTest.getEstoque();
        product = ProductServiceImplTest.getProduto();
        sale = SaleServiceImplTest.getVenda();
    }

    @Test
    @DisplayName("Deve salvar um produto referente a uma Venda com sucesso")
    void salvarProdutoVenda() {
        //Cenário
        stock.setQuantity(9);
        product.setStock(stock);
        int qtdEstoqueAntesDeSalvar = stock.getQuantity();

        when(mapper.map(productsOfSaleDTO, ProductsOfSale.class)).thenReturn(productsOfSale);
        when(saleService.findSaleById(productsOfSaleDTO.getSaleId())).thenReturn(sale);
        ProductDTO productDTO = productsOfSaleDTO.getProduct();
		when(productService.findProductById(productDTO.getId())).thenReturn(product);
        when(productsOfSaleRepository
                .existsProductsOfSalesBySaleIdAndProductId(sale.getId(), product.getId())).thenReturn(false);
        when(productsOfSaleRepository.save(productsOfSale)).thenReturn(productsOfSale);

        //Execução
        ProductsOfSale productsOfSaleSalvo = produtoVendaService.saveProductsOfSale(productsOfSaleDTO);

        //Verificação
        assertNotNull(productsOfSaleSalvo);
        assertNotNull(productsOfSaleSalvo.getSale());
        assertNotNull(productsOfSaleSalvo.getProduct());
        assertNotNull(productsOfSaleSalvo.getUnitValue());
        assertNotNull(productsOfSaleSalvo.getTotalValue());
        assertEquals(productsOfSale.getProduct().getSalePrice(), productsOfSaleSalvo.getUnitValue());
        BigDecimal valorTotalTest = productsOfSaleSalvo.getUnitValue()
                .multiply(BigDecimal.valueOf(productsOfSale.getQuantity()));
        assertEquals(valorTotalTest, productsOfSaleSalvo.getTotalValue());
        assertEquals(qtdEstoqueAntesDeSalvar - productsOfSale.getQuantity(), stock.getQuantity());

        verify(stockService, times(1)).updateStock(any());
        verify(productsOfSaleRepository, times(1)).save(productsOfSale);
    }

    @Test
    @DisplayName("Deve lancar erro quando a Venda informada não existir")
    void erroSalvarProdutoVendaComVenda() {
        //Cenário
        when(saleService.findSaleById(productsOfSaleDTO.getSaleId()))
                .thenThrow(new ObjectNotFoundException("Venda não encontrada para o Id informado."));

        //Execução e verificação
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            produtoVendaService.saveProductsOfSale(productsOfSaleDTO);
        });

        assertEquals("Venda não encontrada para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro quando o produto informado não existir")
    void erroSalvarProdutoVendaComProduto() {
        //Cenário
        when(mapper.map(productsOfSaleDTO, ProductsOfSale.class)).thenReturn(productsOfSale);
        when(saleService.findSaleById(productsOfSaleDTO.getSaleId())).thenReturn(sale);
        ProductDTO productDTO = productsOfSaleDTO.getProduct();
		when(productService.findProductById(productDTO.getId()))
                .thenThrow(new ObjectNotFoundException("Produto não encontrado para o Id informado."));

        //Execução e verificação
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            produtoVendaService.saveProductsOfSale(productsOfSaleDTO);
        });

        assertEquals("Produto não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro quando a quantidade produto for maior do que a quantidade disponível em estoque")
    void erroSalvarProdutoVendaEstoque() {
        //Cenário
        productsOfSale.setQuantity(11);
        stock.setQuantity(10);
        product.setStock(stock);

        when(mapper.map(productsOfSaleDTO, ProductsOfSale.class)).thenReturn(productsOfSale);
        when(saleService.findSaleById(productsOfSaleDTO.getSaleId())).thenReturn(sale);
        ProductDTO productDTO = productsOfSaleDTO.getProduct();
		when(productService.findProductById(productDTO.getId())).thenReturn(product);

        //Execução e verificação
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            produtoVendaService.saveProductsOfSale(productsOfSaleDTO);
        });

        assertEquals("Não é possível realizar a Venda pois a quantidade solicitada do produto é maior " +
                "do que a quantidade disponível em estoque.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro quando o produto ja estiver sido adicionado à Venda")
    void erroSalvarProdutoVendaProdutoJaAdicionadoAoVenda() {
        //Cenário
        product.setStock(stock);

        when(mapper.map(productsOfSaleDTO, ProductsOfSale.class)).thenReturn(productsOfSale);
        when(saleService.findSaleById(productsOfSaleDTO.getSaleId())).thenReturn(sale);
        ProductDTO productDTO = productsOfSaleDTO.getProduct();
		when(productService.findProductById(productDTO.getId())).thenReturn(product);
        when(productsOfSaleRepository.existsProductsOfSalesBySaleIdAndProductId(sale.getId(), product.getId()))
                .thenReturn(true);

        //Execução e verificação
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            produtoVendaService.saveProductsOfSale(productsOfSaleDTO);
        });

        assertEquals("Erro ao tentar Salvar, Produto já adicionado à Venda.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar uma lista de ProdutoVenda")
    void buscarTodosProdutoVenda() {
        //Cenário
        List<ProductsOfSale> listaProductsOfSale = new ArrayList<>();
        listaProductsOfSale.add(productsOfSale);
        listaProductsOfSale.add(productsOfSale);
        listaProductsOfSale.add(productsOfSale);

        when(productsOfSaleRepository.findAll()).thenReturn(listaProductsOfSale);

        //Execução
        List<ProductsOfSale> produtosVendasRetornados = produtoVendaService.findAllProductsOfSale();

        //Verificação
        assertNotNull(produtosVendasRetornados);
        assertEquals(3, produtosVendasRetornados.size());
        assertEquals(listaProductsOfSale, produtosVendasRetornados);
        assertEquals(ArrayList.class, produtosVendasRetornados.getClass());
    }

    @Test
    @DisplayName("Deve buscar um ProdutoVenda por id com sucesso")
    void buscarProdutoVendaPorId() {
        //Cenário
        when(productsOfSaleRepository.existsById(anyInt())).thenReturn(true);
        when(productsOfSaleRepository.findById(anyInt())).thenReturn(Optional.of(productsOfSale));

        //Execução
        ProductsOfSale productsOfSaleBuscado = produtoVendaService.findProductsOfSaleById(anyInt());

        //Verificação
        assertNotNull(productsOfSaleBuscado);
        assertEquals(productsOfSale, productsOfSaleBuscado);
        assertEquals(ProductsOfSale.class, productsOfSaleBuscado.getClass());
    }

    @Test
    @DisplayName("Deve atualizar um ProdutoVenda com sucesso, com o mesmo produto anterior")
    void atualizarProdutoVenda() {
        //Cenário
        stock.setQuantity(10);
        int qtdEstoqueAntigo = stock.getQuantity();
        int qtdAnteriorProduto = productsOfSale.getQuantity();
        int novaQtdProduto = productsOfSaleDTO.getQuantity();
        product.setStock(stock);
        productsOfSale.setProduct(product);

        when(productsOfSaleRepository.existsById(productsOfSaleDTO.getId())).thenReturn(true);
        when(productsOfSaleRepository.findById(productsOfSaleDTO.getId()))
                .thenReturn(Optional.of(productsOfSale));
        when(saleService.findSaleById(productsOfSaleDTO.getSaleId())).thenReturn(sale);
        ProductDTO productDTO = productsOfSaleDTO.getProduct();
		when(productService.findProductById(productDTO.getId())).thenReturn(product);
        when(productsOfSaleRepository.save(productsOfSale)).thenReturn(productsOfSale);

        //Execução
        ProductsOfSale productsOfSaleAtualizado = produtoVendaService.updateProductsOfSale(productsOfSaleDTO);

        //Verificação
        assertNotNull(productsOfSaleAtualizado);
        assertEquals(sale, productsOfSaleAtualizado.getSale());
        assertEquals(product, productsOfSaleAtualizado.getProduct());
        assertEquals(stock, productsOfSaleAtualizado.getProduct().getStock());
        int qtdEstoqueAtualizado = productsOfSaleAtualizado.getProduct().getStock().getQuantity();
        assertEquals(qtdEstoqueAntigo + qtdAnteriorProduto - novaQtdProduto, qtdEstoqueAtualizado);
        assertEquals(productsOfSale.getProduct().getSalePrice(), productsOfSale.getUnitValue());
        BigDecimal valorTotal = productsOfSale.getUnitValue().multiply(
                BigDecimal.valueOf(productsOfSaleDTO.getQuantity())
        );
        assertEquals(valorTotal, productsOfSaleAtualizado.getTotalValue());
        verify(stockService, times(1)).updateStock(any());
        verify(productsOfSaleRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar atualizar ProdutoVenda com estoque indisponivel, com o mesmo produto")
    void atualizarProdutoVendaComErroNaValidacaoDoEstoque() {
        //Cenário
        stock.setQuantity(10);
        productsOfSaleDTO.setQuantity(15);
        product.setStock(stock);
        productsOfSale.setProduct(product);

        when(productsOfSaleRepository.existsById(productsOfSaleDTO.getId())).thenReturn(true);
        when(productsOfSaleRepository.findById(productsOfSaleDTO.getId()))
                .thenReturn(Optional.of(productsOfSale));
        when(saleService.findSaleById(productsOfSaleDTO.getSaleId())).thenReturn(sale);
        ProductDTO productDTO = productsOfSaleDTO.getProduct();
		when(productService.findProductById(productDTO.getId())).thenReturn(product);

        //Execução e verificação
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            produtoVendaService.updateProductsOfSale(productsOfSaleDTO);
        });
        assertEquals("Não é possível Atualizar a Venda pois a quantidade solicitada do Produto" +
                " é maior do que a quantidade disponível em estoque.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar um ProdutoVenda com sucesso, com produto diferente do anterior")
    void atualizarProdutoVendaComOutroProduto() {
        //Cenário
        Stock stockNovoProduto = Stock.builder()
                .id(5)
                .minStock(6)
                .maxStock(15)
                .quantity(8)
                .build();
        Product novoProduct = Product.builder()
                .id(15)
                .name("Capacete TAM-55")
                .costPrice(BigDecimal.valueOf(55.50))
                .salePrice(BigDecimal.valueOf(130.80))
                .brand("Samarino")
                .build();
        novoProduct.setStock(stockNovoProduto);

        product.setId(7);
        product.setStock(stock);
        productsOfSale.setProduct(product);
        Integer qtdProdutoVendaAntigo = productsOfSale.getQuantity();
        Integer qtdEstoqueAntigo = productsOfSale.getProduct().getStock().getQuantity();
        Integer qtdEstoqueNovoProdutoAntes = stockNovoProduto.getQuantity();

        when(productsOfSaleRepository.existsById(productsOfSaleDTO.getId())).thenReturn(true);
        when(productsOfSaleRepository.findById(productsOfSaleDTO.getId()))
                .thenReturn(Optional.of(productsOfSale));
        when(saleService.findSaleById(productsOfSaleDTO.getSaleId())).thenReturn(sale);
        ProductDTO productDTO = productsOfSaleDTO.getProduct();
		when(productService.findProductById(productDTO.getId())).thenReturn(novoProduct);
        when(productsOfSaleRepository.save(productsOfSale)).thenReturn(productsOfSale);

        //Execução
        ProductsOfSale productsOfSaleAtualizado = produtoVendaService.updateProductsOfSale(productsOfSaleDTO);

        //Verificação
        assertNotNull(productsOfSaleAtualizado);
        assertEquals(qtdProdutoVendaAntigo + qtdEstoqueAntigo, stock.getQuantity());

        Product novoProductAtualizado = productsOfSaleAtualizado.getProduct();
        Stock stockNovoProdutoAtualizado = novoProductAtualizado.getStock();

        assertEquals(qtdEstoqueNovoProdutoAntes - productsOfSaleDTO.getQuantity(),
                stockNovoProdutoAtualizado.getQuantity());
        assertEquals(sale, productsOfSaleAtualizado.getSale());
        assertEquals(novoProduct, productsOfSaleAtualizado.getProduct());
        assertEquals(productsOfSaleDTO.getQuantity(), productsOfSaleAtualizado.getQuantity());
        assertEquals(novoProduct.getSalePrice(), productsOfSaleAtualizado.getUnitValue());
        BigDecimal valorTotal = productsOfSaleAtualizado.getUnitValue().multiply(
                BigDecimal.valueOf(productsOfSaleDTO.getQuantity())
        );
        assertEquals(valorTotal, productsOfSaleAtualizado.getTotalValue());

        verify(stockService, times(2)).updateStock(any());
        verify(productsOfSaleRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lancar erro quando atualizar o ProdutoVenda trocando o " +
            "produto com quantidade indisponível do novo produto no estoque")
    void atualizarProdutoVendaComOutroProdutoEQtdIndisponivel() {
        //Cenário
        Product novoProduct = Product.builder()
                .id(15)
                .name("Capacete TAM-55")
                .costPrice(BigDecimal.valueOf(55.50))
                .salePrice(BigDecimal.valueOf(130.80))
                .brand("Samarino")
                .build();
        novoProduct.setStock(stock);

        productsOfSaleDTO.setQuantity(1500);
        product.setId(7);
        product.setStock(stock);
        productsOfSale.setProduct(product);

        when(productsOfSaleRepository.existsById(productsOfSaleDTO.getId())).thenReturn(true);
        when(productsOfSaleRepository.findById(productsOfSaleDTO.getId()))
                .thenReturn(Optional.of(productsOfSale));
        when(saleService.findSaleById(productsOfSaleDTO.getSaleId())).thenReturn(sale);
        ProductDTO productDTO = productsOfSaleDTO.getProduct();
		when(productService.findProductById(productDTO.getId())).thenReturn(novoProduct);
        when(productsOfSaleRepository.save(productsOfSale)).thenReturn(productsOfSale);

        //Execução e verificação
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            produtoVendaService.updateProductsOfSale(productsOfSaleDTO);
        });
        assertEquals("Não é possível Atualizar a Venda pois a quantidade solicitada " +
                "do novo Produto é maior do que a quantidade disponível em estoque.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao validar a existencia de um ProdutoVenda")
    void atualizarProdutoVendaNaoExistente() {
        //Cenário
        when(productsOfSaleRepository.existsById(productsOfSaleDTO.getId())).thenReturn(false);

        //Execução e verificação
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            produtoVendaService.updateProductsOfSale(productsOfSaleDTO);
        });
        assertEquals("Produto da Venda não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro quando o produto já estiver cadastrado na Venda")
    void atualizarProdutoVendaJaCadastradoNaVenda() {
        //Cenário
        productsOfSale.setProduct(product);
        productsOfSale.setSale(sale);

        List<ProductsOfSale> listaProductsOfSale = new ArrayList<>();
        listaProductsOfSale.add(productsOfSale);
        listaProductsOfSale.add(productsOfSale);

        when(productsOfSaleRepository.existsById(productsOfSaleDTO.getId())).thenReturn(true);
        when(productsOfSaleRepository.findByIdNot(productsOfSale.getId())).thenReturn(listaProductsOfSale);
        when(productsOfSaleRepository.findById(productsOfSale.getId())).thenReturn(Optional.of(productsOfSale));

        //Execução e verificação
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            produtoVendaService.updateProductsOfSale(productsOfSaleDTO);
        });
        assertEquals("Erro ao tentar Atualizar, Produto já adicionado à Venda.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar um ProdutoVenda por id com sucesso")
    void deletarProdutoVendaPorId() {
        //Cenário
        productsOfSale.setProduct(product);
        when(productsOfSaleRepository.existsById(anyInt())).thenReturn(true);
        when(productsOfSaleRepository.findById(anyInt())).thenReturn(Optional.of(productsOfSale));
        doNothing().when(productsOfSaleRepository).deleteById(anyInt());

        //Execução
        produtoVendaService.deleteProductsOfSaleById(anyInt());

        //Verificação
        verify(stockService, times(1)).addStockQuantity(anyInt(), anyInt());
        verify(productsOfSaleRepository, times(1)).deleteById(anyInt());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um ProdutoVenda por id")
    void deletarProdutoVendaPorIdComErro() {
        //Cenário
        productsOfSale.setProduct(product);
        when(productsOfSaleRepository.existsById(anyInt())).thenReturn(false);

        //Execução e verificação
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            produtoVendaService.deleteProductsOfSaleById(anyInt());
        });
        assertEquals("Produto da Venda não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar uma lista de ProdutoVenda por id da Venda")
    void buscarProdutoVendaPorIdVenda() {
        //Cenário
        Integer idVenda = 1;
        List<ProductsOfSale> listaProductsOfSale = new ArrayList<>();
        listaProductsOfSale.add(productsOfSale);
        listaProductsOfSale.add(productsOfSale);
        listaProductsOfSale.add(productsOfSale);

        when(productsOfSaleRepository.findProductsOfSaleBySaleId(idVenda))
                .thenReturn(listaProductsOfSale);

        //Execução
        List<ProductsOfSale> listaProductsOfSaleRetornada = produtoVendaService
                .findProductsOfSaleBySaleId(idVenda);

        //Verificação
        assertNotNull(listaProductsOfSaleRetornada);
        assertEquals(3, listaProductsOfSaleRetornada.size());
        assertEquals(ArrayList.class, listaProductsOfSaleRetornada.getClass());
    }

    @Test
    @DisplayName("Deve lancar erro porque o ProdutoVenda por id nao existe")
    void validarProdutoVendaQuandoIdNaoExiste() {
        //Cenário
        Integer id = 1;
        when(productsOfSaleRepository.existsById(id)).thenReturn(false);

        //Execução e verificação
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            produtoVendaService.validateProductsOfSaleById(id);
        });
        assertEquals("Produto da Venda não encontrado para o Id informado.", exception.getMessage());
        verify(productsOfSaleRepository, times(1)).existsById(id);
    }

    @Test
    @DisplayName("Nao deve lancar erro porque o ProdutoVenda por id existe")
    void validarProdutoVendaQuandoIdExiste() {
        //Cenário
        Integer id = 1;
        when(productsOfSaleRepository.existsById(id)).thenReturn(true);

        //Execução e verificação
        assertDoesNotThrow(
                () -> produtoVendaService.validateProductsOfSaleById(id)
        );
        verify(productsOfSaleRepository, times(1)).existsById(id);
    }

    public static ProductsOfSale getProdutoVenda() {
        return ProductsOfSale.builder()
                .id(1)
                .sale(null)
                .product(null)
                .quantity(3)
                .unitValue(null)
                .totalValue(null)
                .build();
    }

    public static ProductsOfSaleDTO getProdutoVendaDTO() {
        return ProductsOfSaleDTO.builder()
                .id(1)
                .saleId(1)
                .product(ProductDTO.builder().id(1).build())
                .quantity(5)
                .unitValue(null)
                .totalValue(null)
                .build();
    }
}