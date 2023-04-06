package com.example.jbmotos.model.repositories;

import com.example.jbmotos.model.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Integer> {

    Optional<Servico> findServicoByPedidoId(Integer idPedido);

    boolean existsServicoByPedidoId(Integer idPedido);

    List<Servico> findServicoByFuncionarioCpf(String cpfFuncionario);
}
