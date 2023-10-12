package com.jbmotos.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.ProdutoDTO;
import com.jbmotos.model.entity.Estoque;
import com.jbmotos.model.entity.Fornecedor;
import com.jbmotos.model.entity.Produto;
import com.jbmotos.model.enums.Situacao;
import com.jbmotos.model.repositories.ProdutoRepository;
import com.jbmotos.services.EstoqueService;
import com.jbmotos.services.FornecedorService;
import com.jbmotos.services.ProdutoService;
import com.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.jbmotos.services.exception.RegraDeNegocioException;

@Service
public class ProdutoServiceImpl implements ProdutoService {

	private final String PRODUTO_NAO_ENCONTRADO = "Produto não encontrado para o Id informado.";

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
	public Produto salvarProduto(ProdutoDTO produtoDTO) {
		estoqueService.verificarUsoEstoque(produtoDTO.getIdEstoque());
		Produto produto = mapper.map(produtoDTO, Produto.class);
		produto.setStatusProduto(Situacao.ATIVO);

		Estoque estoque = estoqueService.buscarEstoquePorId(produtoDTO.getIdEstoque());
		produto.setEstoque(estoque);

		Fornecedor fornecedor = fornecedorService.buscarFornecedorPorCNPJ(produtoDTO.getCnpjFornecedor());
		produto.setFornecedor(fornecedor);

		return produtoRepository.save(produto);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Produto> buscarTodosProdutos() {
		return produtoRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Produto buscarProdutoPorId(Integer id) {
		return produtoRepository.findById(id)
				.orElseThrow(() -> new ObjetoNaoEncontradoException(PRODUTO_NAO_ENCONTRADO));
	}

	@Override
	public List<Produto> filtrarProduto(ProdutoDTO produtoDTO) {
		Example<Produto> example = Example.of(mapper.map(produtoDTO, Produto.class),
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
		return produtoRepository.findAll(example);
	}

	@Override
	@Transactional
	public Situacao alternarStatusProduto(Integer idProduto) {
		Produto produto = buscarProdutoPorId(idProduto);
		if (produto.getStatusProduto().equals(Situacao.ATIVO)) {
			produto.setStatusProduto(Situacao.INATIVO);
		} else if (produto.getStatusProduto().equals(Situacao.INATIVO)) {
			produto.setStatusProduto(Situacao.ATIVO);
		}
		produtoRepository.save(produto);
		return produto.getStatusProduto();
	}

	@Override
	@Transactional
	public Produto atualizarProduto(ProdutoDTO produtoDTO) {
		Produto produto = mapper.map(produtoDTO, Produto.class);
		validarEstoqueParaAtualizar(produtoDTO);

		LocalDateTime dateTime = buscarProdutoPorId(produtoDTO.getId()).getDataHoraCadastro();
		produto.setDataHoraCadastro(dateTime);

		Estoque estoque = estoqueService.buscarEstoquePorId(produtoDTO.getIdEstoque());
		produto.setEstoque(estoque);

		Fornecedor fornecedor = fornecedorService.buscarFornecedorPorCNPJ(produtoDTO.getCnpjFornecedor());
		produto.setFornecedor(fornecedor);

		return produtoRepository.save(produto);
	}

	@Override
	@Transactional
	public void deletarProduto(Integer id) {
		verificaSeProdutoExiste(id);
		produtoRepository.deleteById(id);
	}

	@Override
	@Transactional
	public BigDecimal calcularLucroProduto(Integer idProduto) {
		Produto produto = buscarProdutoPorId(idProduto);
		return produto.getPrecoVenda().subtract(produto.getPrecoCusto());
	}

	public void validarEstoqueParaAtualizar(ProdutoDTO produtoDTO) {
		filtrarProdutosPorIdDiferente(produtoDTO).stream().forEach(produtoFiltrado -> {
			if (produtoDTO.getIdEstoque().equals(produtoFiltrado.getEstoque().getId())) {
				throw new RegraDeNegocioException("Erro ao tentar Atualizar, o Estoque já pertence a um Produto.");
			}
		});
	}

	private List<Produto> filtrarProdutosPorIdDiferente(ProdutoDTO produtoDTO) {
		return buscarTodosProdutos().stream().filter(produto -> (!produtoDTO.getId().equals(produto.getId())))
				.collect(Collectors.toList());
	}

	@Override
	public void verificaSeProdutoExiste(Integer id) {
		if (!produtoRepository.existsById(id)) {
			throw new ObjetoNaoEncontradoException(PRODUTO_NAO_ENCONTRADO);
		}
	}

	@Override
	public boolean existeProdutoPorIdEstoque(Integer idEstoque) {
		return produtoRepository.existsProdutoByEstoqueId(idEstoque);
	}
}
