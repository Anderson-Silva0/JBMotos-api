package com.example.jbmotos.services;

import com.example.jbmotos.api.dto.MotoDTO;
import com.example.jbmotos.model.entity.Moto;

import java.util.List;
import java.util.Optional;

public interface MotoService {

    Moto salvarMoto(MotoDTO motoDTO);

    List<Moto> buscarTodasMotos();

    List<Moto> buscarMotosPorCpfCliente(String cpfCliente);

    Optional<Moto> buscarMotoPorId(Integer idMoto);

    Optional<Moto> buscarMotoPorPlaca(String placa);

    Moto atualizarMoto(MotoDTO motoDTO);

    void deletarMotoPorId(Integer idMoto);

    void deletarMotoPorPlaca(String placa);

    void validarPlacaMotoParaSalvar(String placa);

    void validarPlacaMotoParaAtualizar(MotoDTO motoDTO);

    void validarExistenciaMotoPorId(Integer idMoto);

    void validarExistenciaMotoPorPlaca(String placa);

    void existeMotoPorCpfCliente(String cpfCliente);
}
