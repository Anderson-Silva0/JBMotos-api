package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.ServicoDTO;
import com.example.jbmotos.model.entity.Servico;

import java.util.List;
import java.util.Optional;

public interface ServicoService {

    Servico salvarServico(ServicoDTO servicoDTO);

    List<Servico> buscarTodosServicos();

    Optional<Servico> buscarServicoPorId(Integer idServico);

    Optional<Servico> buscarServicoPorIdPedido(Integer idPedido);

    List<Servico> buscarServicosPorCpfFuncionario(String cpfFuncionario);

    Servico atualizarServico(ServicoDTO servicoDTO);

    void deletarServico(Integer idServico);

    void verificarSePedidoPertenceAoServico(Integer idPedido);

    void validarServico(Integer idServico);
}
