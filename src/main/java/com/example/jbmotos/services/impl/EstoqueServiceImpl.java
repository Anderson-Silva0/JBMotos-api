package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.EstoqueDTO;
import com.example.jbmotos.model.entity.Estoque;
import com.example.jbmotos.model.enums.StatusEstoque;
import com.example.jbmotos.model.repositories.EstoqueRepository;
import com.example.jbmotos.services.EstoqueService;
import com.example.jbmotos.services.ProdutoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class EstoqueServiceImpl implements EstoqueService {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    @Lazy
    private ProdutoService produtoService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Estoque salvarEstoque(EstoqueDTO estoqueDTO) {
        Estoque estoque = mapper.map(estoqueDTO, Estoque.class);
        estoque.setStatus(validarQuantidade(estoque));
        return estoqueRepository.save(estoque);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estoque> buscarTodosEstoques() {
        return estoqueRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Estoque> buscarEstoquePorId(Integer id) {
        validarEstoque(id);
        return estoqueRepository.findById(id);
    }

    @Override
    @Transactional
    public Estoque atualizarEstoque(EstoqueDTO estoqueDTO) {
        validarEstoque(estoqueDTO.getId());
        Estoque estoque = mapper.map(estoqueDTO, Estoque.class);
        estoque.setStatus(validarQuantidade(estoque));
        return estoqueRepository.save(estoque);
    }

    @Override
    @Transactional
    public void deletarEstoquePorId(Integer id) {
        validarEstoque(id);
        verificarUsoEstoque(id);
        estoqueRepository.deleteById(id);
    }

    @Override
    public Integer obterQuantidadeDoProduto(Integer idProduto) {
        return produtoService.buscarProdutoPorId(idProduto).get().getEstoque().getQuantidade();
    }

    @Override
    public void adicionarQuantidadeAoEstoque(Integer idProduto, Integer quantidade) {
        Estoque estoque = produtoService.buscarProdutoPorId(idProduto).get().getEstoque();
        estoque.setQuantidade( estoque.getQuantidade() + quantidade );
        atualizarEstoque(mapper.map(estoque, EstoqueDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularValorTotalEstoque() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (Estoque estoque : buscarTodosEstoques()) {
            valorTotal = valorTotal.add(
                    estoque.getProduto().getPrecoVenda().multiply( BigDecimal.valueOf(estoque.getQuantidade()) )
            );
        }
        return valorTotal;
    }

    @Override
    public void validarEstoque(Integer id) {
        if (!estoqueRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("Estoque não encontrado para o Id Informado.");
        }
    }

    @Override
    public void verificarUsoEstoque(Integer id) {
        if (produtoService.existeProdutoPorIdEstoque(id)) {
            throw new RegraDeNegocioException("O Estoque pertence a um Produto.");
        }
    }

    private StatusEstoque validarQuantidade(Estoque estoque) {
        if (estoque.getQuantidade() > estoque.getEstoqueMaximo()) {
            return StatusEstoque.ESTOQUE_ALTO;
        } else if (estoque.getQuantidade() < estoque.getEstoqueMinimo() && estoque.getQuantidade() > 0) {
            return StatusEstoque.ESTOQUE_BAIXO;
        } else if (estoque.getQuantidade() == 0) {
            return StatusEstoque.INDISPONIVEL;
        }
        return StatusEstoque.DISPONIVEL;
    }
}
