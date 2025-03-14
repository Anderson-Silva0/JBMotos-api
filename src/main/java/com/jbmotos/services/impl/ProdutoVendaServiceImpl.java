package com.jbmotos.services.impl;

import java.math.BigDecimal;
import java.util.List;

import com.jbmotos.api.dto.ProdutoDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.EstoqueDTO;
import com.jbmotos.api.dto.ProdutoVendaDTO;
import com.jbmotos.model.entity.Estoque;
import com.jbmotos.model.entity.Produto;
import com.jbmotos.model.entity.ProdutoVenda;
import com.jbmotos.model.entity.Venda;
import com.jbmotos.model.enums.StatusEstoque;
import com.jbmotos.model.repositories.ProdutoVendaRepository;
import com.jbmotos.services.EstoqueService;
import com.jbmotos.services.ProdutoService;
import com.jbmotos.services.ProdutoVendaService;
import com.jbmotos.services.VendaService;
import com.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.jbmotos.services.exception.RegraDeNegocioException;

@Service
public class ProdutoVendaServiceImpl implements ProdutoVendaService {

	private final String PRODUTO_VENDA_NAO_ENCONTRADO = "Produto da Venda não encontrado para o Id informado.";

	private static final String MSG_ERRO_SALVAR_PRODUTO_VENDA = "Não é possível realizar a Venda pois a "
			+ "quantidade solicitada do produto é maior do que a quantidade disponível em estoque.";

	private static final String MSG_ERRO_ATUALIZAR_PRODUTO_VENDA = "Não é possível Atualizar a Venda pois a "
			+ "quantidade solicitada do Produto é maior do que a quantidade disponível em estoque.";

	private static final String MSG_ERRO_ATUALIZAR_NOVO_PRODUTO = "Não é possível Atualizar a Venda pois a quantidade "
			+ "solicitada do novo Produto é maior do que a quantidade disponível em estoque.";

	@Autowired
	@Lazy
	private VendaService vendaService;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private EstoqueService estoqueService;

	@Autowired
	private ProdutoVendaRepository produtoVendaRepository;

	@Autowired
	private ModelMapper mapper;

	@Override
	@Transactional
	public ProdutoVenda salvarProdutoVenda(ProdutoVendaDTO produtoVendaDTO) {
		ProdutoVenda produtoVenda = obterProdutoVendaParaSalvar(produtoVendaDTO);
		return produtoVendaRepository.save(produtoVenda);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProdutoVenda> buscarTodosProdutoVenda() {
		return produtoVendaRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public ProdutoVenda buscarProdutoVendaPorId(Integer id) {
		return produtoVendaRepository.findById(id)
				.orElseThrow(() -> new ObjetoNaoEncontradoException(PRODUTO_VENDA_NAO_ENCONTRADO));
	}

	@Override
	@Transactional
	public ProdutoVenda atualizarProdutoVenda(ProdutoVendaDTO produtoVendaDTO) {
		validarProdutoVenda(produtoVendaDTO.getId());
		ProdutoVenda produtoVenda = obterProdutoVendaParaAtualizar(produtoVendaDTO);
		return produtoVendaRepository.save(produtoVenda);
	}

	@Override
	@Transactional
	public void deletarProdutoVendaPorId(Integer id) {
		validarProdutoVenda(id);
		atualizarQtdEstoqueParaDeletar(id);
		produtoVendaRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProdutoVenda> buscarProdutoVendaPorIdVenda(Integer idVenda) {
		return produtoVendaRepository.findProdutoVendaByVendaId(idVenda);
	}

	@Override
	@Transactional(readOnly = true)
	public void validarProdutoVenda(Integer id) {
		if (!produtoVendaRepository.existsById(id)) {
			throw new ObjetoNaoEncontradoException(PRODUTO_VENDA_NAO_ENCONTRADO);
		}
	}

	private ProdutoVenda obterProdutoVendaParaSalvar(ProdutoVendaDTO produtoVendaDTO) {
		ProdutoVenda produtoVenda = mapper.map(produtoVendaDTO, ProdutoVenda.class);

		Venda venda = vendaService.buscarVendaPorId(produtoVendaDTO.getIdVenda());
		produtoVenda.setVenda(venda);

		ProdutoDTO produtoDTO = produtoVendaDTO.getProduto();
		Produto produto = produtoService.buscarProdutoPorId(produtoDTO.getId());
		produtoVenda.setProduto(produto);

		verificarSeProdutoJaExisteNaVendaParaSalvar(produtoVenda);

		Estoque estoque = produtoVenda.getProduto().getEstoque();
		validarEstoqueSalvar(produtoVenda.getQuantidade(), estoque.getQuantidade(), estoque.getStatus());

		produtoVenda.setValorUnidade(produtoVenda.getProduto().getPrecoVenda());
		produtoVenda.setValorTotal(
				produtoVenda.getValorUnidade().multiply(BigDecimal.valueOf(produtoVenda.getQuantidade())));
		atualizarQtdEstoqueParaSalvar(produtoVenda);

		return produtoVenda;
	}

	private ProdutoVenda obterProdutoVendaParaAtualizar(ProdutoVendaDTO produtoVendaDTO) {
		ProdutoVenda produtoVenda = buscarProdutoVendaPorId(produtoVendaDTO.getId());

		verificarSeProdutoJaExisteNaVendaParaAtualizar(produtoVenda, produtoVendaDTO);

		ProdutoDTO produtoDTO = produtoVendaDTO.getProduto();
		Integer produtoDtoId = produtoDTO.getId();

		if (!produtoDtoId.equals(produtoVenda.getProduto().getId())) {
			atualizarVendaComNovoProduto(produtoVenda, produtoVendaDTO);
		} else {
			Venda venda = vendaService.buscarVendaPorId(produtoVendaDTO.getIdVenda());
			produtoVenda.setVenda(venda);

			Produto produto = produtoService.buscarProdutoPorId(produtoDtoId);
			produtoVenda.setProduto(produto);

			Estoque estoque = produtoVenda.getProduto().getEstoque();
			validarEstoqueAtualizar(estoque.getQuantidade(), produtoVendaDTO.getQuantidade(),
					produtoVenda.getQuantidade());
			atualizarQtdEstoqueParaAtualizar(produtoVenda, produtoVendaDTO);
		}

		produtoVenda.setValorUnidade(produtoVenda.getProduto().getPrecoVenda());
		produtoVenda.setValorTotal(
				produtoVenda.getValorUnidade().multiply(BigDecimal.valueOf(produtoVendaDTO.getQuantidade())));

		return produtoVenda;
	}

	private void atualizarVendaComNovoProduto(ProdutoVenda produtoVenda, ProdutoVendaDTO produtoVendaDTO) {
		Integer qtdProdutoVendaAntigo = produtoVenda.getQuantidade();
		Integer qtdEstoqueAntigo = produtoVenda.getProduto().getEstoque().getQuantidade();

		// Devolver a quantidade de estoque do produto antigo, pois mudou de produto.
		Estoque estoqueProdutoAntigo = produtoVenda.getProduto().getEstoque();
		estoqueProdutoAntigo.setQuantidade(qtdProdutoVendaAntigo + qtdEstoqueAntigo);
		estoqueService.atualizarEstoque(mapper.map(estoqueProdutoAntigo, EstoqueDTO.class));

		ProdutoDTO produtoDTO = produtoVendaDTO.getProduto();

		// Abater a quantidade de estoque do novo produto.
		Produto novoProduto = produtoService.buscarProdutoPorId(produtoDTO.getId());

		Estoque estoqueNovoProduto = novoProduto.getEstoque();

		if (produtoVendaDTO.getQuantidade() > novoProduto.getEstoque().getQuantidade()) {
			throw new RegraDeNegocioException(MSG_ERRO_ATUALIZAR_NOVO_PRODUTO);
		}

		estoqueNovoProduto.setQuantidade(estoqueNovoProduto.getQuantidade() - produtoVendaDTO.getQuantidade());

		Venda venda = vendaService.buscarVendaPorId(produtoVendaDTO.getIdVenda());
		produtoVenda.setVenda(venda);

		produtoVenda.setProduto(novoProduto);
		produtoVenda.setQuantidade(produtoVendaDTO.getQuantidade());

		estoqueService.atualizarEstoque(mapper.map(estoqueNovoProduto, EstoqueDTO.class));
	}

	private void atualizarQtdEstoqueParaSalvar(ProdutoVenda produtoVenda) {
		Estoque estoque = produtoVenda.getProduto().getEstoque();
		estoque.setQuantidade(estoque.getQuantidade() - produtoVenda.getQuantidade());
		estoqueService.atualizarEstoque(mapper.map(estoque, EstoqueDTO.class));
	}

	private void atualizarQtdEstoqueParaAtualizar(ProdutoVenda produtoVenda, ProdutoVendaDTO produtoVendaDTO) {
		Estoque estoque = produtoVenda.getProduto().getEstoque();

		Integer qtdAtualEstoque = estoque.getQuantidade();
		Integer qtdAnteriorProduto = produtoVenda.getQuantidade();
		Integer qtdNovaProduto = produtoVendaDTO.getQuantidade();

		Integer novaQtdEstoque = qtdAtualEstoque + qtdAnteriorProduto - qtdNovaProduto;
		estoque.setQuantidade(novaQtdEstoque);

		produtoVenda.setQuantidade(qtdNovaProduto);

		estoqueService.atualizarEstoque(mapper.map(estoque, EstoqueDTO.class));
	}

	@Transactional(readOnly = true)
	@Override
	public void atualizarQtdEstoqueParaDeletar(Integer id) {
		ProdutoVenda produtoVenda = buscarProdutoVendaPorId(id);
		estoqueService.adicionarQuantidadeAoEstoque(produtoVenda.getProduto().getId(), produtoVenda.getQuantidade());
	}

	private void validarEstoqueSalvar(Integer qtdProduto, Integer qtdEstoque, StatusEstoque status) {
		if (status == StatusEstoque.INDISPONIVEL) {
			throw new RegraDeNegocioException("Estoque indisponível.");
		} else if (qtdProduto > qtdEstoque) {
			throw new RegraDeNegocioException(MSG_ERRO_SALVAR_PRODUTO_VENDA);
		}
	}

	private void validarEstoqueAtualizar(Integer qtdAtualEstoque, Integer qtdNovaProduto, Integer qtdAnteriorProduto) {
		if (qtdAtualEstoque + qtdAnteriorProduto - qtdNovaProduto < 0) {
			throw new RegraDeNegocioException(MSG_ERRO_ATUALIZAR_PRODUTO_VENDA);
		}
	}

	private void verificarSeProdutoJaExisteNaVendaParaSalvar(ProdutoVenda produtoVenda) {
		Venda venda = produtoVenda.getVenda();
		Produto produto = produtoVenda.getProduto();
		if (produtoVendaRepository.existsProdutoVendasByVendaIdAndProdutoId(venda.getId(), produto.getId())) {
			throw new RegraDeNegocioException("Erro ao tentar Salvar, Produto já adicionado à Venda.");
		}
	}

	private void verificarSeProdutoJaExisteNaVendaParaAtualizar(ProdutoVenda produtoVenda, ProdutoVendaDTO dto) {
		filtrarProdutoVendaPorIdDiferente(produtoVenda).forEach(produtoVendaFiltrado -> {
			ProdutoDTO produtoDTO = dto.getProduto();
			if (produtoDTO.getId().equals(produtoVendaFiltrado.getProduto().getId())
					&& dto.getIdVenda().equals(produtoVendaFiltrado.getVenda().getId())) {
				throw new RegraDeNegocioException("Erro ao tentar Atualizar, Produto já adicionado à Venda.");
			}
		});
	}

	private List<ProdutoVenda> filtrarProdutoVendaPorIdDiferente(ProdutoVenda produtoVenda) {
		return produtoVendaRepository.findByIdNot(produtoVenda.getId());
	}
}
