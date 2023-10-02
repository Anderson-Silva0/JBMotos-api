package com.jbmotos.model.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbmotos.model.entity.Servico;

public interface ServicoRepository extends JpaRepository<Servico, Integer> {

    Optional<Servico> findServicoByPedidoId(Integer idPedido);

    boolean existsServicoByPedidoId(Integer idPedido);

    List<Servico> findServicoByFuncionarioCpf(String cpfFuncionario);
}
