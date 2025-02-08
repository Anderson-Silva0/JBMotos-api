package com.jbmotos.model.repositories;

import com.jbmotos.model.entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Integer> {

    @Query("SELECT v FROM Venda v WHERE v.dataHoraCadastro >= :dataInicio AND v.dataHoraCadastro < :dataFim")
    List<Venda> getVendasDoMesAtual(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);
}