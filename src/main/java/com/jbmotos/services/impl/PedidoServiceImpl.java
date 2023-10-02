package com.jbmotos.services.impl;

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

import com.jbmotos.api.dto.PedidoDTO;
import com.jbmotos.model.entity.Cliente;
import com.jbmotos.model.entity.Funcionario;
import com.jbmotos.model.entity.Pedido;
import com.jbmotos.model.entity.Produto;
import com.jbmotos.model.entity.ProdutoPedido;
import com.jbmotos.model.repositories.PedidoRepository;
import com.jbmotos.services.ClienteService;
import com.jbmotos.services.FuncionarioService;
import com.jbmotos.services.PedidoService;
import com.jbmotos.services.ProdutoPedidoService;
import com.jbmotos.services.ProdutoService;
import com.jbmotos.services.exception.ObjetoNaoEncontradoException;

@Service
public class PedidoServiceImpl implements PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private ProdutoPedidoService produtoPedidoService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FuncionarioService funcionarioService;

	@Autowired
	private ModelMapper mapper;

	@Override
	@Transactional
	public Pedido salvarPedido(PedidoDTO pedidoDTO) {
		pedidoDTO.setDataHoraCadastro(LocalDateTime.now());
		Pedido pedido = mapper.map(pedidoDTO, Pedido.class);

		Optional<Cliente> clienteOptional = clienteService.buscarClientePorCPF(pedidoDTO.getCpfCliente());
		if (clienteOptional.isPresent()) {
			pedido.setCliente(clienteOptional.get());
		}

		Optional<Funcionario> funcionarioOptional = funcionarioService
				.buscarFuncionarioPorCPF(pedidoDTO.getCpfFuncionario());
		if (funcionarioOptional.isPresent()) {
			pedido.setFuncionario(funcionarioOptional.get());
		}

		return pedidoRepository.save(pedido);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Pedido> buscarTodosPedidos() {
		return pedidoRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Pedido> buscarPedidoPorId(Integer id) {
		validarPedido(id);
		return pedidoRepository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Pedido> filtrarPedido(PedidoDTO pedidoDTO) {
		Example<Pedido> example = Example.of(mapper.map(pedidoDTO, Pedido.class),
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
		return pedidoRepository.findAll(example);
	}

	@Override
	@Transactional
	public Pedido atualizarPedido(PedidoDTO pedidoDTO) {
		Optional<Pedido> pedidoOptional = buscarPedidoPorId(pedidoDTO.getId());
		if (pedidoOptional.isPresent()) {
			LocalDateTime dateTime = pedidoOptional.get().getDataHoraCadastro();
			Pedido pedido = mapper.map(pedidoDTO, Pedido.class);

			Optional<Cliente> clienteOptional = clienteService.buscarClientePorCPF(pedidoDTO.getCpfCliente());
			if (clienteOptional.isPresent()) {
				pedido.setCliente(clienteOptional.get());
			}

			Optional<Funcionario> funcionarioOptional = funcionarioService
					.buscarFuncionarioPorCPF(pedidoDTO.getCpfFuncionario());
			if (funcionarioOptional.isPresent()) {
				pedido.setFuncionario(funcionarioOptional.get());
			}

			pedido.setDataHoraCadastro(dateTime);
			return pedidoRepository.save(pedido);
		}
		return null;
	}

	@Override
	@Transactional
	public void deletarPedido(Integer id) {
		validarPedido(id);

		Optional<Pedido> pedidoOptional = buscarPedidoPorId(id);
		if (pedidoOptional.isPresent()) {
			Pedido pedido = pedidoOptional.get();

			pedido.getProdutosPedido().stream().forEach(produto -> {
				produtoPedidoService.atualizarQtdEstoqueParaDeletar(produto.getId());
			});
			pedidoRepository.deleteById(id);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal calcularLucroDoPedido(Integer idPedido) {
		validarPedido(idPedido);
		List<ProdutoPedido> produtosDoPedido = produtoPedidoService.buscarProdutoPedidoPorIdPedido(idPedido);
		return produtosDoPedido.stream()
				.map(produtoPedido -> produtoService.calcularLucroProduto(produtoPedido.getProduto().getId())
						.multiply(BigDecimal.valueOf(produtoPedido.getQuantidade())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Override
	public void validarPedido(Integer id) {
		if (!pedidoRepository.existsById(id)) {
			throw new ObjetoNaoEncontradoException("Pedido não encontrado para o Id informado.");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal valorTotalDoPedido(Integer idPedido) {
		validarPedido(idPedido);
		return produtoPedidoService.buscarProdutoPedidoPorIdPedido(idPedido).stream()
				.map(ProdutoPedido::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Produto> buscarProdutosDoPedido(Integer idPedido) {
		return produtoPedidoService.buscarProdutoPedidoPorIdPedido(idPedido).stream()
				.map(ProdutoPedido::getProduto).collect(Collectors.toList());
	}
}