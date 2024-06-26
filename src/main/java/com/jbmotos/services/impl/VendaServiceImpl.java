package com.jbmotos.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.PagamentoCartaoDTO;
import com.jbmotos.api.dto.ProdutoVendaDTO;
import com.jbmotos.api.dto.VendaDTO;
import com.jbmotos.model.entity.Cliente;
import com.jbmotos.model.entity.Funcionario;
import com.jbmotos.model.entity.Produto;
import com.jbmotos.model.entity.ProdutoVenda;
import com.jbmotos.model.entity.Venda;
import com.jbmotos.model.repositories.VendaRepository;
import com.jbmotos.services.ClienteService;
import com.jbmotos.services.FuncionarioService;
import com.jbmotos.services.PagamentoCartaoService;
import com.jbmotos.services.ProdutoService;
import com.jbmotos.services.ProdutoVendaService;
import com.jbmotos.services.VendaService;
import com.jbmotos.services.exception.ObjetoNaoEncontradoException;

@Service
public class VendaServiceImpl implements VendaService {

	private final String VENDA_NAO_ENCONTRADA = "Venda não encontrada para o Id informado.";

	@Autowired
	private VendaRepository vendaRepository;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private ProdutoVendaService produtoVendaService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FuncionarioService funcionarioService;
	
	@Lazy
	@Autowired
	private PagamentoCartaoService pagamentoCartaoService;

	@Autowired
	private ModelMapper mapper;

	@Override
	@Transactional
	public Venda salvarVenda(VendaDTO vendaDTO) {
		Venda venda = mapper.map(vendaDTO, Venda.class);
		
		Cliente cliente = clienteService.buscarClientePorCPF(vendaDTO.getCpfCliente());
		venda.setCliente(cliente);
		
		Funcionario funcionario = funcionarioService.buscarFuncionarioPorCPF(vendaDTO.getCpfFuncionario());
		venda.setFuncionario(funcionario);
		
		List<ProdutoVendaDTO> produtosVenda = vendaDTO.getProdutosVenda();
		
		venda.setProdutosVenda(new ArrayList<>());
		venda.setPagamentoCartao(null);
		Venda vendaSalva = vendaRepository.save(venda);
		
		if (produtosVenda != null) {
			for (ProdutoVendaDTO produtoVenda : produtosVenda) {
				produtoVenda.setIdVenda(vendaSalva.getId());
				produtoVendaService.salvarProdutoVenda(produtoVenda);
			}
		}
		
		if (vendaDTO.getFormaDePagamento().equals("Cartão de Crédito")) {
			PagamentoCartaoDTO pagamentoCartaoDTO = vendaDTO.getPagamentoCartao();
			pagamentoCartaoDTO.setIdVenda(vendaSalva.getId());
			
			pagamentoCartaoService.salvarPagamentoCartao(pagamentoCartaoDTO);
		}

		return venda;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Venda> buscarTodasVendas() {
		return vendaRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Venda buscarVendaPorId(Integer id) {
		return vendaRepository.findById(id)
				.orElseThrow(() -> new ObjetoNaoEncontradoException(VENDA_NAO_ENCONTRADA));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Venda> filtrarVenda(VendaDTO vendaDTO) {
		Example<Venda> example = Example.of(mapper.map(vendaDTO, Venda.class),
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
		return vendaRepository.findAll(example);
	}

	@Override
	@Transactional
	public Venda atualizarVenda(VendaDTO vendaDTO) {
		Venda venda = mapper.map(vendaDTO, Venda.class);

		LocalDateTime dateTime = buscarVendaPorId(vendaDTO.getId()).getDataHoraCadastro();
		venda.setDataHoraCadastro(dateTime);

		Cliente cliente = clienteService.buscarClientePorCPF(vendaDTO.getCpfCliente());
		venda.setCliente(cliente);

		Funcionario funcionario = funcionarioService.buscarFuncionarioPorCPF(vendaDTO.getCpfFuncionario());
		venda.setFuncionario(funcionario);

		return vendaRepository.save(venda);
	}

	@Override
	@Transactional
	public void deletarVenda(Integer id) {
		validarVenda(id);

		Venda venda = buscarVendaPorId(id);

		venda.getProdutosVenda().stream().forEach(produto -> {
			produtoVendaService.atualizarQtdEstoqueParaDeletar(produto.getId());
		});

		vendaRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal calcularLucroDaVenda(Integer idVenda) {
		validarVenda(idVenda);
		List<ProdutoVenda> produtosDaVenda = produtoVendaService.buscarProdutoVendaPorIdVenda(idVenda);
		return produtosDaVenda.stream()
				.map(produtoVenda -> produtoService.calcularLucroProduto(produtoVenda.getProduto().getId())
						.multiply(BigDecimal.valueOf(produtoVenda.getQuantidade())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Override
	public void validarVenda(Integer id) {
		if (!vendaRepository.existsById(id)) {
			throw new ObjetoNaoEncontradoException(VENDA_NAO_ENCONTRADA);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal valorTotalDaVenda(Integer idVenda) {
		validarVenda(idVenda);
		return produtoVendaService.buscarProdutoVendaPorIdVenda(idVenda).stream()
				.map(ProdutoVenda::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Produto> buscarProdutosDaVenda(Integer idVenda) {
		return produtoVendaService.buscarProdutoVendaPorIdVenda(idVenda).stream()
				.map(ProdutoVenda::getProduto).collect(Collectors.toList());
	}
}