package com.jbmotos.services.impl;

import java.math.BigDecimal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.EstoqueDTO;
import com.jbmotos.model.entity.Estoque;
import com.jbmotos.model.entity.Produto;
import com.jbmotos.model.enums.StatusEstoque;
import com.jbmotos.model.repositories.EstoqueRepository;
import com.jbmotos.services.EstoqueService;
import com.jbmotos.services.ProdutoService;
import com.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.jbmotos.services.exception.RegraDeNegocioException;

@Service
public class EstoqueServiceImpl implements EstoqueService {

	private final String ESTOQUE_NAO_ENCONTRADO = "Estoque não encontrado para o Id informado.";

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
        estoque.setStatus(obterStatusEstoque(estoque));
        return estoqueRepository.save(estoque);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estoque> buscarTodosEstoques() {
        return estoqueRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Estoque buscarEstoquePorId(Integer id) {
        return estoqueRepository.findById(id)
        		.orElseThrow(() -> new ObjetoNaoEncontradoException(ESTOQUE_NAO_ENCONTRADO));
    }

    @Override
    @Transactional
    public Estoque atualizarEstoque(EstoqueDTO estoqueDTO) {
        validarEstoque(estoqueDTO.getId());
        Estoque estoque = mapper.map(estoqueDTO, Estoque.class);
        estoque.setStatus(obterStatusEstoque(estoque));
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
		Produto produto = produtoService.buscarProdutoPorId(idProduto);
		return produto.getEstoque().getQuantidade();
	}

	@Override
	@Transactional
	public void adicionarQuantidadeAoEstoque(Integer idProduto, Integer quantidade) {
		Produto produto = produtoService.buscarProdutoPorId(idProduto);
		Estoque estoque = produto.getEstoque();
		estoque.setQuantidade(estoque.getQuantidade() + quantidade);
		atualizarEstoque(mapper.map(estoque, EstoqueDTO.class));
	}

	@Override
    @Transactional(readOnly = true)
    public BigDecimal calcularCustoTotalEstoque() {
        return buscarTodosEstoques().stream()
                .map(estoque -> estoque.getProduto().getPrecoCusto()
                        .multiply(BigDecimal.valueOf(estoque.getQuantidade()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularPotencialVendaEstoque() {
        return buscarTodosEstoques().stream()
                .map(estoque -> estoque.getProduto().getPrecoVenda()
                        .multiply(BigDecimal.valueOf(estoque.getQuantidade()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void validarEstoque(Integer id) {
        if (!estoqueRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException(ESTOQUE_NAO_ENCONTRADO);
        }
    }

    @Override
    public void verificarUsoEstoque(Integer id) {
        if (produtoService.existeProdutoPorIdEstoque(id)) {
            throw new RegraDeNegocioException("O Estoque pertence a um Produto.");
        }
    }

    private StatusEstoque obterStatusEstoque(Estoque estoque) {
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
