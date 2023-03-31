package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.MotoDTO;
import com.example.jbmotos.model.entity.Moto;
import com.example.jbmotos.model.repositories.MotoRepository;
import com.example.jbmotos.services.ClienteService;
import com.example.jbmotos.services.MotoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        motoDTO.setPlaca(motoDTO.getPlaca().toUpperCase());
        Moto moto = mapper.map(motoDTO, Moto.class);
        validarPlacaMotoParaSalvar(motoDTO.getPlaca());
        moto.setCliente(clienteService.buscarClientePorCPF(motoDTO.getCpfCliente()).get());
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
    @Transactional
    public Moto atualizarMoto(MotoDTO motoDTO) {
        motoDTO.setPlaca(motoDTO.getPlaca().toUpperCase());
        validarExistenciaMotoPorId(motoDTO.getId());
        validarPlacaMotoParaAtualizar(motoDTO);
        Moto moto = mapper.map(motoDTO, Moto.class);
        moto.setCliente(clienteService.buscarClientePorCPF(motoDTO.getCpfCliente()).get());
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
