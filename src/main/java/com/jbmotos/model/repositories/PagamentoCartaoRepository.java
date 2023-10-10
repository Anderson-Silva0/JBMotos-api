package com.jbmotos.model.repositories;

import com.jbmotos.model.entity.PagamentoCartao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagamentoCartaoRepository extends JpaRepository<PagamentoCartao, Integer> {

    boolean existsByVendaId(Integer idVenda);
}
