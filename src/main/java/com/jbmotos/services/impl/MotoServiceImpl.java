package com.jbmotos.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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
        moto.setDataHoraCadastro(LocalDateTime.now());
        moto.setPlaca(motoDTO.getPlaca().toUpperCase());
        validarPlacaMotoParaSalvar(motoDTO.getPlaca());
        moto.setStatusMoto(Situacao.ATIVO);

        Optional<Cliente> clienteOptional = clienteService.buscarClientePorCPF(motoDTO.getCpfCliente());
        if (clienteOptional.isPresent()) {
        	moto.setCliente(clienteOptional.get());
		}

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
    public Optional<Moto> buscarMotoPorId(Integer idMoto) {
        validarExistenciaMotoPorId(idMoto);
        return motoRepository.findById(idMoto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Moto> buscarMotoPorPlaca(String placa) {
        String placaMaiuscula = placa.toUpperCase();
        validarExistenciaMotoPorPlaca(placaMaiuscula);
        return motoRepository.findMotoByPlaca(placaMaiuscula);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Moto> filtrarMoto(MotoDTO motoDTO) {
        Example<Moto> example = Example.of(mapper.map(motoDTO, Moto.class),
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return motoRepository.findAll(example);
    }

	@Override
	@Transactional
	public Situacao alternarStatusMoto(Integer idMoto) {
		Optional<Moto> motoOptional = buscarMotoPorId(idMoto);
		if (motoOptional.isPresent()) {
			if (motoOptional.get().getStatusMoto().equals(Situacao.ATIVO)) {
				motoOptional.get().setStatusMoto(Situacao.INATIVO);
			} else if (motoOptional.get().getStatusMoto().equals(Situacao.INATIVO)) {
				motoOptional.get().setStatusMoto(Situacao.ATIVO);
			}
			motoRepository.save(motoOptional.get());
			return motoOptional.get().getStatusMoto();
		}
		return null;
	}

	@Override
	@Transactional
	public Moto atualizarMoto(MotoDTO motoDTO) {
		Optional<Moto> motoOptional = buscarMotoPorId(motoDTO.getId());
		if (motoOptional.isPresent()) {
			LocalDateTime dateTime = motoOptional.get().getDataHoraCadastro();
			motoDTO.setPlaca(motoDTO.getPlaca().toUpperCase());
			validarExistenciaMotoPorId(motoDTO.getId());
			validarPlacaMotoParaAtualizar(motoDTO);
			Moto moto = mapper.map(motoDTO, Moto.class);

			Optional<Cliente> clienteOptional = clienteService.buscarClientePorCPF(motoDTO.getCpfCliente());
			if (clienteOptional.isPresent()) {
				moto.setCliente(clienteOptional.get());
			}

			moto.setDataHoraCadastro(dateTime);
			return motoRepository.save(moto);
		}
		return null;
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
            throw new ObjetoNaoEncontradoException("Moto não encontrada para o Id informado.");
        }
    }

    @Override
    public void validarExistenciaMotoPorPlaca(String placa) {
        if (!motoRepository.existsMotoByPlaca(placa)) {
            throw new ObjetoNaoEncontradoException("Moto não encontrada para a Placa informada.");
        }
    }

    @Override
    public void existeMotoPorCpfCliente(String cpfCliente) {
        if (!motoRepository.existsMotoByClienteCpf(cpfCliente)) {
            throw new RegraDeNegocioException("O cliente não possui nenhuma moto cadastrada.");
        }
    }
}
