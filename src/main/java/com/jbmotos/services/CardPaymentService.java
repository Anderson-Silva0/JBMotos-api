package com.jbmotos.services;

import com.jbmotos.api.dto.CardPaymentDTO;
import com.jbmotos.model.entity.CardPayment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CardPaymentService {

    CardPayment saveCardPayment(CardPaymentDTO cardPaymentDTO);

    List<CardPayment> findAllCardPayments();

    CardPayment findCardPaymentById(Integer id);

    CardPayment findCardPaymentBySaleId(Integer saleId);

    CardPayment updateCardPayment(CardPaymentDTO cardPaymentDTO);

    void deleteCardPayment(Integer id);

    void existsCardPaymentById(Integer id);
}
