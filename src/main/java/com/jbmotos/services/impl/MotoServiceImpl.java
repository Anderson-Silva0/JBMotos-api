package com.jbmotos.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.MotoDTO;
import com.jbmotos.model.entity.Cliente;
import com.jbmotos.model.entity.Moto;
import com.jbmotos.model.enums.Situacao;
import com.jbmotos.model.repositories.MotoRepository;
import com.jbmotos.services.ClienteService;
import com.jbmotos.services.MotoService;
import com.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.jbmotos.services.exception.RegraDeNegocioException;

@Service
public class MotoServiceImpl implements MotoService {

	private final String MOTO_NAO_ENCONTRADA_ID = "Moto não encontrada para o Id informado.";
	private final String MOTO_NAO_ENCONTRADA_PLACA = "Moto não encontrada para a Placa informada.";

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public Moto salvarMoto(MotoDTO motoDTO) {
        Moto moto = mapper.map(motoDTO, Moto.class);
        moto.setPlaca(motoDTO.getPlaca().toUpperCase());
        validarPlacaMotoParaSalvar(motoDTO.getPlaca());
        moto.setStatusMoto(Situacao.ATIVO);

        Cliente cliente = clienteService.buscarClientePorCPF(motoDTO.getCpfCliente());
        moto.setCliente(cliente);

        return motoRepository.save(moto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Moto> buscarTodasMotos() {
        return motoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Moto> buscarMotosPorCpfCliente(String cpfCliente) {
        clienteService.checarCpfClienteExistente(cpfCliente);
        existeMotoPorCpfCliente(cpfCliente);
        return motoRepository.findMotosByClienteCpf(cpfCliente);
    }

	@Override
	@Transactional(readOnly = true)
	public Moto buscarMotoPorId(Integer idMoto) {
		return motoRepository.findById(idMoto)
				.orElseThrow(() -> new ObjetoNaoEncontradoException(MOTO_NAO_ENCONTRADA_ID));
	}

	@Override
	@Transactional(readOnly = true)
	public Moto buscarMotoPorPlaca(String placa) {
		String placaMaiuscula = placa.toUpperCase();
		return motoRepository.findMotoByPlaca(placaMaiuscula)
				.orElseThrow(() -> new ObjetoNaoEncontradoException(MOTO_NAO_ENCONTRADA_PLACA));
	}

    @Override
    @Transactional(readOnly = true)
    public List<Moto> filtrarMoto(MotoDTO motoDTO) {
        Example<Moto> example = Example.of(mapper.map(motoDTO, Moto.class),
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        Sort sort = Sort.by(Sort.Direction.DESC, "dataHoraCadastro");

        return motoRepository.findAll(example, sort);
    }

	@Override
	@Transactional
	public Situacao alternarStatusMoto(Integer idMoto) {
		Moto moto = buscarMotoPorId(idMoto);
		if (moto.getStatusMoto().equals(Situacao.ATIVO)) {
			moto.setStatusMoto(Situacao.INATIVO);
		} else if (moto.getStatusMoto().equals(Situacao.INATIVO)) {
			moto.setStatusMoto(Situacao.ATIVO);
		}
		motoRepository.save(moto);
		return moto.getStatusMoto();
	}

	@Override
	@Transactional
	public Moto atualizarMoto(MotoDTO motoDTO) {
		Moto moto = mapper.map(motoDTO, Moto.class);

		LocalDateTime dateTime = buscarMotoPorId(motoDTO.getId()).getDataHoraCadastro();
		moto.setDataHoraCadastro(dateTime);

		validarPlacaMotoParaAtualizar(motoDTO);
		motoDTO.setPlaca(motoDTO.getPlaca().toUpperCase());

		Cliente cliente = clienteService.buscarClientePorCPF(motoDTO.getCpfCliente());
		moto.setCliente(cliente);

		return motoRepository.save(moto);
	}

    @Override
    @Transactional
    public void deletarMotoPorId(Integer idMoto) {
        validarExistenciaMotoPorId(idMoto);
        motoRepository.deleteById(idMoto);
    }

    @Override
    @Transactional
    public void deletarMotoPorPlaca(String placa) {
        String placaMaiuscula = placa.toUpperCase();
        validarExistenciaMotoPorPlaca(placaMaiuscula);
        motoRepository.deleteByPlaca(placaMaiuscula);
    }

    @Override
    public void validarPlacaMotoParaSalvar(String placa) {
        if (motoRepository.existsMotoByPlaca(placa)) {
            throw new RegraDeNegocioException("Erro ao tentar salvar, Placa já cadastrada.");
        }
    }

    @Override
    public void validarPlacaMotoParaAtualizar(MotoDTO motoDTO) {
        filtrarMotosPorIdDiferente(motoDTO.getId()).stream().forEach(motoFiltrada -> {
            if (motoDTO.getPlaca().equals(motoFiltrada.getPlaca())) {
                throw new RegraDeNegocioException("Erro ao tentar atualizar Moto, Placa já cadastrada.");
            }
        });
    }

    private List<Moto> filtrarMotosPorIdDiferente(Integer idMoto) {
        return motoRepository.findByIdNot(idMoto);
    }

    @Override
    public void validarExistenciaMotoPorId(Integer idMoto) {
        if (!motoRepository.existsById(idMoto)) {
            throw new ObjetoNaoEncontradoException(MOTO_NAO_ENCONTRADA_ID);
        }
    }

    @Override
    public void validarExistenciaMotoPorPlaca(String placa) {
        if (!motoRepository.existsMotoByPlaca(placa)) {
            throw new ObjetoNaoEncontradoException(MOTO_NAO_ENCONTRADA_PLACA);
        }
    }

    @Override
    public void existeMotoPorCpfCliente(String cpfCliente) {
        if (!motoRepository.existsMotoByClienteCpf(cpfCliente)) {
            throw new RegraDeNegocioException("O cliente não possui nenhuma moto cadastrada.");
        }
    }
}
