package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.ProdutoDTO;
import com.example.jbmotos.model.entity.Produto;
import com.example.jbmotos.model.repositories.ProdutoRepository;
import com.example.jbmotos.services.EstoqueService;
import com.example.jbmotos.services.FornecedorService;
import com.example.jbmotos.services.ProdutoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueService estoqueService;

    @Autowired
    private FornecedorService fornecedorService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Produto salvarProduto(ProdutoDTO produtoDTO){
        estoqueService.verificarUsoEstoque(produtoDTO.getIdEstoque());
        Produto produto = mapper.map(produtoDTO, Produto.class);
        produto.setEstoque(estoqueService.buscarEstoquePorId(produtoDTO.getIdEstoque()).get());
        produto.setFornecedor(fornecedorService.buscarFornecedorPorCNPJ(produtoDTO.getCnpjFornecedor()).get());
        return produtoRepository.save(produto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Produto> buscarTodosProdutos(){
        return produtoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Produto> buscarProdutoPorId(Integer id){
        verificaSeProdutoExiste(id);
        return produtoRepository.findById(id);
    }

    @Override
    @Transactional
    public Produto atualizarProduto(ProdutoDTO produtoDTO){
        verificaSeProdutoExiste(produtoDTO.getId());
        validarEstoqueParaAtualizar(produtoDTO);
        Produto produto = mapper.map(produtoDTO, Produto.class);
        produto.setEstoque(estoqueService.buscarEstoquePorId(produtoDTO.getIdEstoque()).get());
        produto.setFornecedor(fornecedorService.buscarFornecedorPorCNPJ(produtoDTO.getCnpjFornecedor()).get());
        return produtoRepository.save(produto);
    }

    @Override
    @Transactional
    public void deletarProduto(Integer id){
        verificaSeProdutoExiste(id);
        produtoRepository.deleteById(id);
    }

    @Override
    public BigDecimal calcularLucroProduto(Integer idProduto){
        Produto produto = buscarProdutoPorId(idProduto).get();
        BigDecimal lucro = produto.getPrecoVenda().subtract(produto.getPrecoCusto());
        return lucro;
    }

    public void validarEstoqueParaAtualizar(ProdutoDTO produtoDTO) {
        filtrarProdutosPorIdDiferente(produtoDTO).stream().forEach(produtoFiltrado -> {
            if (produtoDTO.getIdEstoque() == produtoFiltrado.getEstoque().getId()) {
                throw new RegraDeNegocioException("Erro ao tentar Atualizar, o Estoque já pertence a um Produto.");
            }
        });
    }

    private List<Produto> filtrarProdutosPorIdDiferente(ProdutoDTO produtoDTO) {
        return buscarTodosProdutos().stream()
                .filter(produto -> (!produtoDTO.getId().equals(produto.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public void verificaSeProdutoExiste(Integer id){
        if (!produtoRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("Produto não encontrado para o Id informado.");
        }
    }

    @Override
    public boolean existeProdutoPorIdEstoque(Integer idEstoque){
        return produtoRepository.existsProdutoByEstoqueId(idEstoque);
    }
}
