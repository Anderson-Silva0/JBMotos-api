package com.jbmotos.model.repositories;

import com.jbmotos.model.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ServicoRepository extends JpaRepository<Servico, Integer> {

    Optional<Servico> findServicoByVendaId(Integer idVenda);

    boolean existsServicoByVendaId(Integer idVenda);

    List<Servico> findServicoByFuncionarioCpf(String cpfFuncionario);

    @Query("SELECT s FROM Servico s WHERE s.dataHoraCadastro >= :dataInicio AND s.dataHoraCadastro < :dataFim")
    List<Servico> getServicosDoMesAtual(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);
}
