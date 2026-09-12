package com.jbmotos.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jbmotos.api.dto.ProductDTO;
import com.jbmotos.model.entity.Product;
import com.jbmotos.model.entity.Stock;
import com.jbmotos.model.entity.Supplier;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.model.enums.StockStatus;
import com.jbmotos.model.repositories.ProductRepository;
import com.jbmotos.services.StockService;
import com.jbmotos.services.SupplierService;
import com.jbmotos.services.exception.BusinessRuleException;
import com.jbmotos.services.exception.ObjectNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockService stockService;

    @Mock
    private SupplierService supplierService;

    @Mock
    private ModelMapper mapper;

    private Product product;
    private ProductDTO productDTO;
    private Stock stock;
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        stock = Stock.builder()
                .id(1)
                .minStock(5)
                .maxStock(20)
                .quantity(10)
                .status(StockStatus.AVAILABLE)
                .build();

        supplier = SupplierServiceImplTest.getFornecedor();
        product = getProduto();
        product.setStock(stock);
        product.setSupplier(supplier);
        product.setCreatedAt(LocalDateTime.now());
        productDTO = getProdutoDTO();
        productDTO.setStockId(stock.getId());
        productDTO.setSupplierCnpj(supplier.getCnpj());
    }

    @Test
    @DisplayName("Deve salvar um produto com sucesso")
    void saveProduct() {
        when(mapper.map(productDTO, Product.class)).thenReturn(product);
        when(stockService.findStockById(productDTO.getStockId())).thenReturn(stock);
        when(supplierService.findSupplierByCnpj(productDTO.getSupplierCnpj())).thenReturn(supplier);
        when(productRepository.save(product)).thenReturn(product);

        Product produtoSalvo = productService.saveProduct(productDTO);

        assertNotNull(produtoSalvo);
        assertEquals(product.getName(), produtoSalvo.getName());
        assertEquals(stock, produtoSalvo.getStock());
        assertEquals(supplier, produtoSalvo.getSupplier());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Deve retornar todos os produtos")
    void findAllProducts() {
        List<Product> produtos = List.of(product, product);
        when(productRepository.findAll()).thenReturn(produtos);

        List<Product> resultado = productService.findAllProducts();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(produtos, resultado);
    }

    @Test
    @DisplayName("Deve buscar um produto por id com sucesso")
    void findProductById() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Product resultado = productService.findProductById(product.getId());

        assertNotNull(resultado);
        assertEquals(product, resultado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar um produto por id inexistente")
    void erroFindProductById() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                productService.findProductById(product.getId()));

        assertEquals("Produto não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar um produto com sucesso")
    void updateProduct() {
        Product produtoExistente = Product.builder()
                .id(product.getId())
                .name("Pneu original")
                .costPrice(BigDecimal.valueOf(90.00))
                .salePrice(BigDecimal.valueOf(140.00))
                .brand("Pirelli")
                .productStatus(Situation.ACTIVE)
                .createdAt(LocalDateTime.now().minusDays(1))
                .stock(stock)
                .supplier(supplier)
                .build();

        Product produtoAtualizado = Product.builder()
                .id(product.getId())
                .name(productDTO.getName())
                .costPrice(productDTO.getCostPrice())
                .salePrice(productDTO.getSalePrice())
                .brand(productDTO.getBrand())
                .productStatus(Situation.ACTIVE)
                .createdAt(null)
                .stock(stock)
                .supplier(supplier)
                .build();

        when(productRepository.findById(productDTO.getId())).thenReturn(Optional.of(produtoExistente));
        when(productRepository.findAll()).thenReturn(List.of(produtoExistente));
        when(mapper.map(productDTO, Product.class)).thenReturn(produtoAtualizado);
        when(stockService.findStockById(productDTO.getStockId())).thenReturn(stock);
        when(supplierService.findSupplierByCnpj(productDTO.getSupplierCnpj())).thenReturn(supplier);
        when(productRepository.save(produtoAtualizado)).thenReturn(produtoAtualizado);

        Product resultado = productService.updateProduct(productDTO);

        assertNotNull(resultado);
        assertEquals(productDTO.getName(), resultado.getName());
        assertEquals(productDTO.getCostPrice(), resultado.getCostPrice());
        assertEquals(productDTO.getSalePrice(), resultado.getSalePrice());
        assertEquals(stock, resultado.getStock());
        assertEquals(supplier, resultado.getSupplier());
    }

    @Test
    @DisplayName("Deve deletar um produto com sucesso")
    void deleteProduct() {
        when(productRepository.existsById(product.getId())).thenReturn(true);

        productService.deleteProduct(product.getId());

        verify(productRepository, times(1)).deleteById(product.getId());
    }

    @Test
    @DisplayName("Deve calcular o lucro do produto")
    void calculateProductProfit() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        BigDecimal lucro = productService.calculateProductProfit(product.getId());

        assertEquals(BigDecimal.valueOf(50.0), lucro);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar um produto para um estoque já utilizado")
    void validateStockToUpdate() {
        Stock stockDuplicado = Stock.builder()
                .id(productDTO.getStockId())
                .minStock(1)
                .maxStock(10)
                .quantity(5)
                .status(StockStatus.AVAILABLE)
                .build();

        Product produtoComMesmoEstoque = Product.builder()
                .id(99)
                .name("Outros Pneus")
                .costPrice(BigDecimal.valueOf(80.00))
                .salePrice(BigDecimal.valueOf(120.00))
                .brand("GoodYear")
                .productStatus(Situation.ACTIVE)
                .createdAt(LocalDateTime.now())
                .stock(stockDuplicado)
                .supplier(supplier)
                .build();

        when(productRepository.findAll()).thenReturn(List.of(produtoComMesmoEstoque));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                productService.validateStockToUpdate(productDTO));

        assertEquals("Erro ao tentar Atualizar, o Estoque já pertence a um Produto.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve verificar se existem produtos pelo id do estoque")
    void existsProductByStockId() {
        when(productRepository.existsProductByStockId(stock.getId())).thenReturn(true);

        boolean resultado = productService.existsProductByStockId(stock.getId());

        assertEquals(true, resultado);
    }

    @Test
    @DisplayName("Deve alternar o status do produto")
    void toggleProductStatus() {
        product.setProductStatus(Situation.ACTIVE);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Situation status = productService.toggleProductStatus(product.getId());

        assertEquals(Situation.INACTIVE, status);
    }

    @Test
    @DisplayName("Deve verificar se o produto existe por id")
    void existsProductById() {
        when(productRepository.existsById(product.getId())).thenReturn(true);

        productService.existsProductById(product.getId());

        verify(productRepository, times(1)).existsById(product.getId());
    }

    public static Product getProduto() {
        return Product.builder()
                .id(1)
                .name("Pneu")
                .costPrice(BigDecimal.valueOf(100.00))
                .salePrice(BigDecimal.valueOf(150.00))
                .brand("Vipal")
                .stock(null)
                .supplier(null)
                .productStatus(Situation.ACTIVE)
                .build();
    }

    public static ProductDTO getProdutoDTO() {
        return ProductDTO.builder()
                .id(1)
                .name("Pneu")
                .costPrice(BigDecimal.valueOf(100.00))
                .salePrice(BigDecimal.valueOf(150.00))
                .brand("Vipal")
                .stockId(1)
                .supplierCnpj("21.300.144/0001-33")
                .build();
    }
}