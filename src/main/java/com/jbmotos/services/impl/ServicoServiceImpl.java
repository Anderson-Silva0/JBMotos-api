package com.jbmotos.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.ServicoDTO;
import com.jbmotos.api.dto.VendaDTO;
import com.jbmotos.model.entity.Funcionario;
import com.jbmotos.model.entity.Moto;
import com.jbmotos.model.entity.Servico;
import com.jbmotos.model.entity.Venda;
import com.jbmotos.model.repositories.ServicoRepository;
import com.jbmotos.services.FuncionarioService;
import com.jbmotos.services.MotoService;
import com.jbmotos.services.ServicoService;
import com.jbmotos.services.VendaService;
import com.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.jbmotos.services.exception.RegraDeNegocioException;

@Service
public class ServicoServiceImpl implements ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private MotoService motoService;

    @Autowired
    private VendaService vendaService;
    
    @Autowired
    private ModelMapper mapper;

	@Override
	@Transactional
	public Servico salvarServico(ServicoDTO servicoDTO) {
		Servico servico = mapper.map(servicoDTO, Servico.class);
		Venda vendaSalva = null;
		VendaDTO vendaDTO = servicoDTO.getVenda();
		
		if (vendaDTO != null) {
			vendaSalva = vendaService.salvarVenda(vendaDTO);
		}

		Funcionario funcionario = funcionarioService.buscarFuncionarioPorCPF(servicoDTO.getCpfFuncionario());
		servico.setFuncionario(funcionario);

		Moto moto = motoService.buscarMotoPorId(servicoDTO.getMoto().getId());
		servico.setMoto(moto);

		servico.setVenda(vendaSalva);

		return servicoRepository.save(servico);
	}

    @Override
    @Transactional(readOnly = true)
    public List<Servico> buscarTodosServicos() {
        return servicoRepository.findAll();
    }

	@Override
	@Transactional(readOnly = true)
	public Servico buscarServicoPorId(Integer idServico) {
		return servicoRepository.findById(idServico)
				.orElseThrow(() -> new ObjetoNaoEncontradoException("Serviço não encontrado para o Id informado."));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Servico> filtrarServico(ServicoDTO servicoDTO) {
		Example<Servico> example = Example.of(mapper.map(servicoDTO, Servico.class),
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
		return servicoRepository.findAll(example);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Servico buscarServicoPorIdVenda(Integer idVenda) {
		vendaService.validarVenda(idVenda);
		return servicoRepository.findServicoByVendaId(idVenda)
				.orElseThrow(() -> new RegraDeNegocioException("A Venda informada não pertence a um Serviço."));
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
		Servico servicoNovo = mapper.map(servicoDTO, Servico.class);

		Servico servicoAntigo = buscarServicoPorId(servicoDTO.getId());
		servicoNovo.setDataHoraCadastro(servicoAntigo.getDataHoraCadastro());

		Funcionario funcionario = funcionarioService.buscarFuncionarioPorCPF(servicoDTO.getCpfFuncionario());
		servicoNovo.setFuncionario(funcionario);

		Moto moto = motoService.buscarMotoPorId(servicoDTO.getMoto().getId());
		servicoNovo.setMoto(moto);

		servicoNovo.setVenda(servicoAntigo.getVenda());

		return servicoRepository.save(servicoNovo);
	}

    @Override
    @Transactional
    public void deletarServico(Integer idServico) {
        validarServico(idServico);
        servicoRepository.deleteById(idServico);
    }

	@Override
	public void verificarSeVendaPertenceAoServico(Integer idVenda) {
		Venda venda = vendaService.buscarVendaPorId(idVenda);
		if (venda.getServico() == null) {
			throw new RegraDeNegocioException("A Venda informada não pertence a um Serviço.");
		}
	}

    @Override
    public void validarServico(Integer idServico) {
        if (!servicoRepository.existsById(idServico)) {
            throw new ObjetoNaoEncontradoException("Serviço não encontrado para o Id informado.");
        }
    }
}
