package com.jbmotos.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.ServicoDTO;
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

		Funcionario funcionario = funcionarioService.buscarFuncionarioPorCPF(servicoDTO.getCpfFuncionario());
		servico.setFuncionario(funcionario);

		Moto moto = motoService.buscarMotoPorId(servicoDTO.getIdMoto());
		servico.setMoto(moto);

		if (servicoDTO.getIdVenda() != null) {
			validarVendaParaSalvarServico(servicoDTO.getIdVenda());
			Venda venda = vendaService.buscarVendaPorId(servicoDTO.getIdVenda());
			servico.setVenda(venda);
		}

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

		validarVendaParaAtualizarServico(servicoAntigo, servicoDTO);

		Funcionario funcionario = funcionarioService.buscarFuncionarioPorCPF(servicoDTO.getCpfFuncionario());
		servicoNovo.setFuncionario(funcionario);

		Moto moto = motoService.buscarMotoPorId(servicoDTO.getIdMoto());
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

    private void validarVendaParaSalvarServico(Integer idPedido) {
        if (servicoRepository.existsServicoByVendaId(idPedido)) {
            throw new RegraDeNegocioException("Erro ao tentar salvar o Serviço, a Venda pertence a outro Serviço.");
        }
    }

	private void validarVendaParaAtualizarServico(Servico servicoAntigo, ServicoDTO servicoDTO) {
		if (!servicoAntigo.getVenda().getId().equals(servicoDTO.getIdVenda())) {
			throw new RegraDeNegocioException("Erro ao tentar atualizar Serviço, a Venda não pode ser alterada.");
		}
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
