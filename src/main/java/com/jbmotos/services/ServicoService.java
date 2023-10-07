package com.jbmotos.services;

import java.util.List;

import com.jbmotos.api.dto.ServicoDTO;
import com.jbmotos.model.entity.Servico;

public interface ServicoService {

    Servico salvarServico(ServicoDTO servicoDTO);

    List<Servico> buscarTodosServicos();

    Servico buscarServicoPorId(Integer idServico);

    Servico buscarServicoPorIdVenda(Integer idVenda);

    List<Servico> buscarServicosPorCpfFuncionario(String cpfFuncionario);

    Servico atualizarServico(ServicoDTO servicoDTO);

    void deletarServico(Integer idServico);

    void verificarSeVendaPertenceAoServico(Integer idVenda);

    void validarServico(Integer idServico);
}
