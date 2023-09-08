package com.example.jbmotos.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jbmotos.api.dto.ProdutoDTO;
import com.example.jbmotos.model.entity.Estoque;
import com.example.jbmotos.model.entity.Fornecedor;
import com.example.jbmotos.model.entity.Produto;
import com.example.jbmotos.model.enums.Situacao;
import com.example.jbmotos.model.repositories.ProdutoRepository;
import com.example.jbmotos.services.EstoqueService;
import com.example.jbmotos.services.FornecedorService;
import com.example.jbmotos.services.ProdutoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;

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
	public Produto salvarProduto(ProdutoDTO produtoDTO) {
		estoqueService.verificarUsoEstoque(produtoDTO.getIdEstoque());
		Produto produto = mapper.map(produtoDTO, Produto.class);
		produto.setStatusProduto(Situacao.ATIVO);
		produto.setDataHoraCadastro(LocalDateTime.now());

		Optional<Estoque> estoqueOptional = estoqueService.buscarEstoquePorId(produtoDTO.getIdEstoque());
		if (estoqueOptional.isPresent()) {
			produto.setEstoque(estoqueOptional.get());
		}

		Optional<Fornecedor> fornecedorOptional = fornecedorService
				.buscarFornecedorPorCNPJ(produtoDTO.getCnpjFornecedor());
		if (fornecedorOptional.isPresent()) {
			produto.setFornecedor(fornecedorOptional.get());
		}

		return produtoRepository.save(produto);
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
	public List<Produto> filtrarProduto(ProdutoDTO produtoDTO) {
		Example<Produto> example = Example.of(mapper.map(produtoDTO, Produto.class),
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
		return produtoRepository.findAll(example);
	}

	@Override
	@Transactional
	public Situacao alternarStatusProduto(Integer idProduto) {
		Optional<Produto> produtoOptional = buscarProdutoPorId(idProduto);
		if (produtoOptional.isPresent()) {
			if (produtoOptional.get().getStatusProduto().equals(Situacao.ATIVO)) {
				produtoOptional.get().setStatusProduto(Situacao.INATIVO);
			} else if (produtoOptional.get().getStatusProduto().equals(Situacao.INATIVO)) {
				produtoOptional.get().setStatusProduto(Situacao.ATIVO);
			}
			produtoRepository.save(produtoOptional.get());
			return produtoOptional.get().getStatusProduto();
		}
		return null;
	}

	@Override
	@Transactional
	public Produto atualizarProduto(ProdutoDTO produtoDTO) {
		verificaSeProdutoExiste(produtoDTO.getId());
		validarEstoqueParaAtualizar(produtoDTO);
		Produto produto = mapper.map(produtoDTO, Produto.class);

		Optional<Estoque> estoqueOptional = estoqueService.buscarEstoquePorId(produtoDTO.getIdEstoque());
		if (estoqueOptional.isPresent()) {
			produto.setEstoque(estoqueOptional.get());
		}

		Optional<Fornecedor> fornecedorOptional = fornecedorService
				.buscarFornecedorPorCNPJ(produtoDTO.getCnpjFornecedor());
		if (fornecedorOptional.isPresent()) {
			produto.setFornecedor(fornecedorOptional.get());
		}

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
		Optional<Produto> produtoOptional = buscarProdutoPorId(idProduto);
		if (produtoOptional.isPresent()) {
			Produto produto = produtoOptional.get();
			return produto.getPrecoVenda().subtract(produto.getPrecoCusto());
		}
		return null;
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
			throw new ObjetoNaoEncontradoException("Produto não encontrado para o Id informado.");
		}
	}

	@Override
	public boolean existeProdutoPorIdEstoque(Integer idEstoque) {
		return produtoRepository.existsProdutoByEstoqueId(idEstoque);
	}
}
