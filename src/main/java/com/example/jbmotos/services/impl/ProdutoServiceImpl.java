package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.ProdutoDTO;
import com.example.jbmotos.model.entity.Produto;
import com.example.jbmotos.model.repositories.ProdutoRepository;
import com.example.jbmotos.services.ProdutoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Produto salvarProduto(ProdutoDTO produtoDTO) {
        return produtoRepository.save(mapper.map(produtoDTO, Produto.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Produto> buscarTodosProdutos() {
        return produtoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Produto> buscarProdutoPorId(Integer id) {
        verificaSeProdutoExiste(id);
        return produtoRepository.findById(id);
    }

    @Override
    @Transactional
    public Produto atualizarProduto(ProdutoDTO produtoDTO) {
        verificaSeProdutoExiste(produtoDTO.getId());
        return produtoRepository.save(mapper.map(produtoDTO, Produto.class));
    }

    @Override
    @Transactional
    public void deletarProduto(Integer id) {
        verificaSeProdutoExiste(id);
        produtoRepository.deleteById(id);
    }

    @Override
    public void verificaSeProdutoExiste(Integer id) {
        if (!produtoRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("Produto não encontrado para o Id informado.");
        }
    }
}
