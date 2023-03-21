package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.EstoqueDTO;
import com.example.jbmotos.model.entity.Estoque;
import com.example.jbmotos.model.entity.Produto;
import com.example.jbmotos.model.enums.StatusEstoque;
import com.example.jbmotos.model.repositories.EstoqueRepository;
import com.example.jbmotos.services.EstoqueService;
import com.example.jbmotos.services.ProdutoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstoqueServiceImpl implements EstoqueService {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ModelMapper mapper;

    @Override
    public Estoque salvarEstoque(EstoqueDTO estoqueDTO){
        Produto produto = produtoService.buscarProdutoPorId(estoqueDTO.getIdProduto()).get();
        Estoque estoque = mapper.map(estoqueDTO, Estoque.class);
        estoque.setProduto(produto);
        estoque.setStatus(validarQuantidade(estoque));
        return estoqueRepository.save(estoque);
    }

    @Override
    public List<Estoque> buscarTodosEstoques(){
        return null;
    }

    @Override
    public Optional<Estoque> buscarEstoquePorId(Integer id){
        return Optional.empty();
    }

    @Override
    public Estoque atualizarEstoque(EstoqueDTO estoqueDTO){
        return null;
    }

    @Override
    public void deletarEstoquePorId(Integer id){

    }

    @Override
    public void validarEstoque(Integer id){

    }

    @Override
    public void verificarUsoEstoque(Integer id){

    }

    private StatusEstoque validarQuantidade(Estoque estoque){
        if (estoque.getQuantidade() > estoque.getEstoqueMaximo()) {
            return StatusEstoque.ESTOQUE_ALTO;
        } else if (estoque.getQuantidade() < estoque.getEstoqueMinimo()) {
            return StatusEstoque.ESTOQUE_BAIXO;
        } else if (estoque.getQuantidade() == 0) {
            return StatusEstoque.INDISPONIVEL;
        }
        return StatusEstoque.DISPONIVEL;
    }
}
