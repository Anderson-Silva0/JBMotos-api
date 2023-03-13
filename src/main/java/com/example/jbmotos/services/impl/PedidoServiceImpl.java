package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.PedidoDTO;
import com.example.jbmotos.model.entity.Cliente;
import com.example.jbmotos.model.entity.Funcionario;
import com.example.jbmotos.model.entity.Produto;
import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.model.repositories.PedidoRepository;
import com.example.jbmotos.services.ClienteService;
import com.example.jbmotos.services.FuncionarioService;
import com.example.jbmotos.services.ProdutoService;
import com.example.jbmotos.services.PedidoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository PedidoRepository;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Pedido salvarPedido(PedidoDTO PedidoDTO) {
        PedidoDTO.setDataHora(LocalDateTime.now());

        List<Produto> produtos = PedidoDTO.getProdutos().stream().map(idProduto ->
                produtoService.buscarProdutoPorId(idProduto).get()
        ).collect(Collectors.toList());

        Pedido Pedido = mapper.map(PedidoDTO, Pedido.class);

        Pedido.setProdutos(produtos);

        Cliente cliente = clienteService.buscarClientePorCPF(PedidoDTO.getCpfCliente()).get();
        Pedido.setCliente(cliente);

        Funcionario funcionario = funcionarioService.buscarFuncionarioPorCPF(PedidoDTO.getCpfFuncionario()).get();
        Pedido.setFuncionario(funcionario);

        System.out.println("Pedido pronto = " + Pedido);

        return PedidoRepository.save(Pedido);
    }

    public Cliente buscarCliente(String cpf) {
        return clienteService.buscarClientePorCPF(cpf).get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarTodasPedidos() {
        return PedidoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPedidoPorId(Integer id) {
        verificaSePedidoExiste(id);
        return PedidoRepository.findById(id);
    }

    @Override
    @Transactional
    public Pedido atualizarPedido(pedidoDTO pedidoDTO) {
        LocalDateTime dateTime = buscarpedidoPorId(pedidoDTO.getId()).get().getDataHora();
        pedido pedido = mapper.map(pedidoDTO, pedido.class);
        pedido.setCliente(clienteService.buscarClientePorCPF(pedidoDTO.getCpfCliente()).get());
        pedido.setFuncionario(funcionarioService.buscarFuncionarioPorCPF(pedidoDTO.getCpfFuncionario()).get());
        pedido.setDataHora(dateTime);
        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public void deletarpedido(Integer id) {
        verificaSepedidoExiste(id);
        pedidoRepository.deleteById(id);
    }

    @Override
    public void verificaSepedidoExiste(Integer id) {
        if (!pedidoRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("pedido não encontrada para o Id informado.");
        }
    }
}
