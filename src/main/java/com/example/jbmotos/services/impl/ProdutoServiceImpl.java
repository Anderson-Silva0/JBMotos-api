package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.ProdutoDTO;
import com.example.jbmotos.model.entity.Produto;
import com.example.jbmotos.model.repositories.ProdutoRepository;
import com.example.jbmotos.services.ProdutoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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
        return produtoRepository.findById(id);
    }

    @Override
    @Transactional
    public Produto atualizarProduto(ProdutoDTO produtoDTO) {
        Objects.requireNonNull(produtoDTO.getId(), "Erro ao tentar atualizar o Produto. Informe um Id.");
        return produtoRepository.save(mapper.map(produtoDTO, Produto.class));
    }

    @Override
    @Transactional
    public void deletarProduto(Integer id) {
        produtoRepository.deleteById(id);
    }
}
