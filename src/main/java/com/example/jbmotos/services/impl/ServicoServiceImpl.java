package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.ServicoDTO;
import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.model.entity.Servico;
import com.example.jbmotos.model.repositories.ServicoRepository;
import com.example.jbmotos.services.FuncionarioService;
import com.example.jbmotos.services.MotoService;
import com.example.jbmotos.services.PedidoService;
import com.example.jbmotos.services.ServicoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ServicoServiceImpl implements ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private MotoService motoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Servico salvarServico(ServicoDTO servicoDTO) {
        servicoDTO.setDataHoraCadastro(LocalDateTime.now());
        validarPedidoParaSalvarServico(servicoDTO.getIdPedido());
        Servico servico = mapper.map(servicoDTO, Servico.class);
        servico.setFuncionario(funcionarioService.buscarFuncionarioPorCPF(servicoDTO.getCpfFuncionario()).get());
        servico.setMoto(motoService.buscarMotoPorId(servicoDTO.getIdMoto()).get());
        servico.setPedido(pedidoService.buscarPedidoPorId(servicoDTO.getIdPedido()).get());
        return servicoRepository.save(servico);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Servico> buscarTodosServicos() {
        return servicoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Servico> buscarServicoPorId(Integer idServico) {
        validarServico(idServico);
        return servicoRepository.findById(idServico);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Servico> buscarServicoPorIdPedido(Integer idPedido) {
        pedidoService.validarPedido(idPedido);
        verificarSePedidoPertenceAoServico(idPedido);
        return servicoRepository.findServicoByPedidoId(idPedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Servico> buscarServicosPorCpfFuncionario(String cpfFuncionario) {
        funcionarioService.checarCpfFuncionarioExistente(cpfFuncionario);
        return servicoRepository.findServicoByFuncionarioCpf(cpfFuncionario);
    }

    @Override
    @Transactional
    public Servico atualizarServico(ServicoDTO servicoDTO) {
        Servico servicoAntigo = buscarServicoPorId(servicoDTO.getId()).get();
        Servico servicoNovo = mapper.map(servicoDTO, Servico.class);
        servicoNovo.setDataHoraCadastro(servicoAntigo.getDataHoraCadastro());
        validarPedidoParaAtualizarServico(servicoAntigo, servicoDTO);
        servicoNovo.setFuncionario(funcionarioService.buscarFuncionarioPorCPF(servicoDTO.getCpfFuncionario()).get());
        servicoNovo.setMoto(motoService.buscarMotoPorId(servicoDTO.getIdMoto()).get());
        servicoNovo.setPedido(servicoAntigo.getPedido());
        return servicoRepository.save(servicoNovo);
    }

    @Override
    @Transactional
    public void deletarServico(Integer idServico) {
        validarServico(idServico);
        servicoRepository.deleteById(idServico);
    }

    private void validarPedidoParaSalvarServico(Integer idPedido) {
        if (servicoRepository.existsServicoByPedidoId(idPedido)) {
            throw new RegraDeNegocioException("Erro ao tentar salvar o Serviço, o pedido pertence a outro Serviço.");
        }
    }

    private void validarPedidoParaAtualizarServico(Servico servicoAntigo, ServicoDTO servicoDTO) {
        if (servicoAntigo.getPedido().getId() != servicoDTO.getIdPedido()) {
            throw new RegraDeNegocioException("Erro ao tentar atualizar Serviço, o Pedido não pode ser alterado.");
        }
    }
    @Override
    public void verificarSePedidoPertenceAoServico(Integer idPedido) {
        Pedido pedido = pedidoService.buscarPedidoPorId(idPedido).get();
        if (pedido.getServico() == null) {
            throw new RegraDeNegocioException("O Pedido informado não pertence a um Serviço.");
        }
    }

    @Override
    public void validarServico(Integer idServico) {
        if (!servicoRepository.existsById(idServico)) {
            throw new ObjetoNaoEncontradoException("Serviço não encontrado para o Id informado.");
        }
    }
}
