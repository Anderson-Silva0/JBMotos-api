package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.EstoqueDTO;
import com.example.jbmotos.api.dto.ProdutoPedidoDTO;
import com.example.jbmotos.model.entity.Estoque;
import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.model.entity.Produto;
import com.example.jbmotos.model.entity.ProdutoPedido;
import com.example.jbmotos.model.enums.StatusEstoque;
import com.example.jbmotos.model.repositories.ProdutoPedidoRepository;
import com.example.jbmotos.services.EstoqueService;
import com.example.jbmotos.services.PedidoService;
import com.example.jbmotos.services.ProdutoPedidoService;
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
public class ProdutoPedidoServiceImpl implements ProdutoPedidoService {

	private final String MSG_ERRO_SALVAR_PRODUTO_PEDIDO = "Não é possível realizar o Pedido pois a "
			+ "quantidade solicitada do produto é maior do que a quantidade disponível em estoque.";

	private final String MSG_ERRO_ATUALIZAR_PRODUTO_PEDIDO = "Não é possível Atualizar o Pedido pois a "
			+ "quantidade solicitada do Produto é maior do que a quantidade disponível em estoque.";

	private final String MSG_ERRO_ATUALIZAR_NOVO_PRODUTO = "Não é possível Atualizar o Pedido pois a quantidade "
			+ "solicitada do novo Produto é maior do que a quantidade disponível em estoque.";

	@Autowired
	@Lazy
	private PedidoService pedidoService;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private EstoqueService estoqueService;

	@Autowired
	private ProdutoPedidoRepository produtoPedidoRepository;

	@Autowired
	private ModelMapper mapper;

	@Override
	@Transactional
	public ProdutoPedido salvarProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO) {
		ProdutoPedido produtoPedido = obterProdutoPedidoParaSalvar(produtoPedidoDTO);
		return produtoPedidoRepository.save(produtoPedido);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProdutoPedido> buscarTodosProdutoPedido() {
		return produtoPedidoRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ProdutoPedido> buscarProdutoPedidoPorId(Integer id) {
		validarProdutoPedido(id);
		return produtoPedidoRepository.findById(id);
	}

	@Override
	@Transactional
	public ProdutoPedido atualizarProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO) {
		validarProdutoPedido(produtoPedidoDTO.getId());
		ProdutoPedido produtoPedido = obterProdutoPedidoParaAtualizar(produtoPedidoDTO);
		return produtoPedidoRepository.save(produtoPedido);
	}

	@Override
	@Transactional
	public void deletarProdutoPedidoPorId(Integer id) {
		validarProdutoPedido(id);
		atualizarQtdEstoqueParaDeletar(id);
		produtoPedidoRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProdutoPedido> buscarProdutoPedidoPorIdPedido(Integer idPedido) {
		return produtoPedidoRepository.findProdutoPedidoByPedidoId(idPedido);
	}

	@Override
	@Transactional(readOnly = true)
	public void validarProdutoPedido(Integer id) {
		if (!produtoPedidoRepository.existsById(id)) {
			throw new ObjetoNaoEncontradoException("Produto do Pedido não encontrado para o Id informado.");
		}
	}

	private ProdutoPedido obterProdutoPedidoParaSalvar(ProdutoPedidoDTO produtoPedidoDTO) {
		ProdutoPedido produtoPedido = mapper.map(produtoPedidoDTO, ProdutoPedido.class);

		Optional<Pedido> pedido = pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido());
		if (pedido.isPresent()) {
			produtoPedido.setPedido(pedido.get());
		}

		Optional<Produto> produto = produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto());
		if (produto.isPresent()) {
			produtoPedido.setProduto(produto.get());
		}

		verificarSeProdutoJaExisteNoPedidoParaSalvar(produtoPedido);

		Estoque estoque = produtoPedido.getProduto().getEstoque();
		validarEstoqueSalvar(produtoPedido.getQuantidade(), estoque.getQuantidade(), estoque.getStatus());

		produtoPedido.setValorUnidade(produtoPedido.getProduto().getPrecoVenda());
		produtoPedido.setValorTotal(
				produtoPedido.getValorUnidade().multiply(BigDecimal.valueOf(produtoPedido.getQuantidade())));
		atualizarQtdEstoqueParaSalvar(produtoPedido);
		return produtoPedido;
	}

	private ProdutoPedido obterProdutoPedidoParaAtualizar(ProdutoPedidoDTO produtoPedidoDTO) {
		Optional<ProdutoPedido> produtoPedidoOptional = buscarProdutoPedidoPorId(produtoPedidoDTO.getId());
		ProdutoPedido produtoPedido = null;

		if (produtoPedidoOptional.isPresent()) {
			produtoPedido = produtoPedidoOptional.get();
			verificarSeProdutoJaExisteNoPedidoParaAtualizar(produtoPedido, produtoPedidoDTO);

			if (!produtoPedidoDTO.getIdProduto().equals(produtoPedido.getProduto().getId())) {
				atualizarPedidoComNovoProduto(produtoPedido, produtoPedidoDTO);
			} else {
				Optional<Pedido> pedido = pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido());
				if (pedido.isPresent()) {
					produtoPedido.setPedido(pedido.get());
				}

				Optional<Produto> produto = produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto());
				if (produto.isPresent()) {
					produtoPedido.setProduto(produto.get());
				}

				Estoque estoque = produtoPedido.getProduto().getEstoque();
				validarEstoqueAtualizar(estoque.getQuantidade(), produtoPedidoDTO.getQuantidade(),
						produtoPedido.getQuantidade());
				atualizarQtdEstoqueParaAtualizar(produtoPedido, produtoPedidoDTO);
			}

			produtoPedido.setValorUnidade(produtoPedido.getProduto().getPrecoVenda());
			produtoPedido.setValorTotal(
					produtoPedido.getValorUnidade().multiply(BigDecimal.valueOf(produtoPedidoDTO.getQuantidade())));
		}

		return produtoPedido;
	}

	private void atualizarPedidoComNovoProduto(ProdutoPedido produtoPedido, ProdutoPedidoDTO produtoPedidoDTO) {
		Integer qtdProdutoPedidoAntigo = produtoPedido.getQuantidade();
		Integer qtdEstoqueAntigo = produtoPedido.getProduto().getEstoque().getQuantidade();

		// Devolver a quantidade de estoque do produto antigo, pois mudou de produto.
		Estoque estoqueProdutoAntigo = produtoPedido.getProduto().getEstoque();
		estoqueProdutoAntigo.setQuantidade(qtdProdutoPedidoAntigo + qtdEstoqueAntigo);
		estoqueService.atualizarEstoque(mapper.map(estoqueProdutoAntigo, EstoqueDTO.class));

		// Abater a quantidade de estoque do novo produto.
		Optional<Produto> novoProdutoOptional = produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto());
		if (novoProdutoOptional.isPresent()) {
			Produto novoProduto = novoProdutoOptional.get();

			Estoque estoqueNovoProduto = novoProduto.getEstoque();

			if (produtoPedidoDTO.getQuantidade() > novoProduto.getEstoque().getQuantidade()) {
				throw new RegraDeNegocioException(MSG_ERRO_ATUALIZAR_NOVO_PRODUTO);
			}

			estoqueNovoProduto.setQuantidade(estoqueNovoProduto.getQuantidade() - produtoPedidoDTO.getQuantidade());

			Optional<Pedido> pedido = pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido());
			if (pedido.isPresent()) {
				produtoPedido.setPedido(pedido.get());
			}

			produtoPedido.setProduto(novoProduto);
			produtoPedido.setQuantidade(produtoPedidoDTO.getQuantidade());

			estoqueService.atualizarEstoque(mapper.map(estoqueNovoProduto, EstoqueDTO.class));
		}
	}

	private void atualizarQtdEstoqueParaSalvar(ProdutoPedido produtoPedido) {
		Estoque estoque = produtoPedido.getProduto().getEstoque();
		estoque.setQuantidade(estoque.getQuantidade() - produtoPedido.getQuantidade());
		estoqueService.atualizarEstoque(mapper.map(estoque, EstoqueDTO.class));
	}

	private void atualizarQtdEstoqueParaAtualizar(ProdutoPedido produtoPedido, ProdutoPedidoDTO produtoPedidoDTO) {
		Estoque estoque = produtoPedido.getProduto().getEstoque();

		Integer qtdAtualEstoque = estoque.getQuantidade();
		Integer qtdAnteriorProduto = produtoPedido.getQuantidade();
		Integer qtdNovaProduto = produtoPedidoDTO.getQuantidade();

		Integer novaQtdEstoque = qtdAtualEstoque + qtdAnteriorProduto - qtdNovaProduto;
		estoque.setQuantidade(novaQtdEstoque);

		produtoPedido.setQuantidade(qtdNovaProduto);

		estoqueService.atualizarEstoque(mapper.map(estoque, EstoqueDTO.class));
	}

	@Transactional(readOnly = true)
	@Override
	public void atualizarQtdEstoqueParaDeletar(Integer id) {
		Optional<ProdutoPedido> produtoPedidoOptional = buscarProdutoPedidoPorId(id);

		if (produtoPedidoOptional.isPresent()) {
			ProdutoPedido produtoPedido = produtoPedidoOptional.get();

			estoqueService.adicionarQuantidadeAoEstoque(produtoPedido.getProduto().getId(),
					produtoPedido.getQuantidade());
		}
	}

	private void validarEstoqueSalvar(Integer qtdProduto, Integer qtdEstoque, StatusEstoque status) {
		if (status == StatusEstoque.INDISPONIVEL) {
			throw new RegraDeNegocioException("Estoque indisponível.");
		} else if (qtdProduto > qtdEstoque) {
			throw new RegraDeNegocioException(MSG_ERRO_SALVAR_PRODUTO_PEDIDO);
		}
	}

	private void validarEstoqueAtualizar(Integer qtdAtualEstoque, Integer qtdNovaProduto, Integer qtdAnteriorProduto) {
		if (qtdAtualEstoque + qtdAnteriorProduto - qtdNovaProduto < 0) {
			throw new RegraDeNegocioException(MSG_ERRO_ATUALIZAR_PRODUTO_PEDIDO);
		}
	}

	private void verificarSeProdutoJaExisteNoPedidoParaSalvar(ProdutoPedido produtoPedido) {
		Pedido pedido = produtoPedido.getPedido();
		Produto produto = produtoPedido.getProduto();
		if (produtoPedidoRepository.existsProdutoPedidosByPedidoIdAndProdutoId(pedido.getId(), produto.getId())) {
			throw new RegraDeNegocioException("Erro ao tentar Salvar, Produto já adicionado ao Pedido.");
		}
	}

	private void verificarSeProdutoJaExisteNoPedidoParaAtualizar(ProdutoPedido produtoPedido, ProdutoPedidoDTO dto) {
		filtrarProdutoPedidoPorIdDiferente(produtoPedido).stream().forEach(produtoPedidoFiltrado -> {
			if (dto.getIdProduto().equals(produtoPedidoFiltrado.getProduto().getId())
					&& dto.getIdPedido().equals(produtoPedidoFiltrado.getPedido().getId())) {
				throw new RegraDeNegocioException("Erro ao tentar Atualizar, Produto já adicionado ao Pedido.");
			}
		});
	}

	private List<ProdutoPedido> filtrarProdutoPedidoPorIdDiferente(ProdutoPedido produtoPedido) {
		return produtoPedidoRepository.findByIdNot(produtoPedido.getId());
	}
}
