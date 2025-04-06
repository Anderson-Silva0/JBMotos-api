package com.jbmotos.model.repositories;

import com.jbmotos.model.entity.CardPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardPaymentRepository extends JpaRepository<CardPayment, Integer> {

    boolean existsBySaleId(Integer saleId);

    Optional<CardPayment> findBySaleId(Integer saleId);
}
