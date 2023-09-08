package com.example.jbmotos.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jbmotos.api.dto.ServicoDTO;
import com.example.jbmotos.model.entity.Funcionario;
import com.example.jbmotos.model.entity.Moto;
import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.model.entity.Servico;
import com.example.jbmotos.model.repositories.ServicoRepository;
import com.example.jbmotos.services.FuncionarioService;
import com.example.jbmotos.services.MotoService;
import com.example.jbmotos.services.PedidoService;
import com.example.jbmotos.services.ServicoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;

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

		Optional<Funcionario> funcionarioOptional = funcionarioService
				.buscarFuncionarioPorCPF(servicoDTO.getCpfFuncionario());
		if (funcionarioOptional.isPresent()) {
			servico.setFuncionario(funcionarioOptional.get());
		}

		Optional<Moto> motoOptional = motoService.buscarMotoPorId(servicoDTO.getIdMoto());
		if (motoOptional.isPresent()) {
			servico.setMoto(motoOptional.get());
		}

		Optional<Pedido> pedidoOptional = pedidoService.buscarPedidoPorId(servicoDTO.getIdPedido());
		if (pedidoOptional.isPresent()) {
			servico.setPedido(pedidoOptional.get());
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
		Optional<Servico> servicoOptional = buscarServicoPorId(servicoDTO.getId());
		if (servicoOptional.isPresent()) {
			Servico servicoAntigo = servicoOptional.get();
			Servico servicoNovo = mapper.map(servicoDTO, Servico.class);
			servicoNovo.setDataHoraCadastro(servicoAntigo.getDataHoraCadastro());
			validarPedidoParaAtualizarServico(servicoAntigo, servicoDTO);

			Optional<Funcionario> funcionarioOptional = funcionarioService
					.buscarFuncionarioPorCPF(servicoDTO.getCpfFuncionario());
			if (funcionarioOptional.isPresent()) {
				servicoNovo.setFuncionario(funcionarioOptional.get());
			}

			Optional<Moto> motoOptional = motoService.buscarMotoPorId(servicoDTO.getIdMoto());
			if (motoOptional.isPresent()) {
				servicoNovo.setMoto(motoOptional.get());
			}

			servicoNovo.setPedido(servicoAntigo.getPedido());
			return servicoRepository.save(servicoNovo);
		}
		return null;
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
		Optional<Pedido> pedidoOptional = pedidoService.buscarPedidoPorId(idPedido);
		if (pedidoOptional.isPresent() && (pedidoOptional.get().getServico() == null)) {
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
