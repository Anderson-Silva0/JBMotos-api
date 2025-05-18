package com.jbmotos.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbmotos.api.dto.CardPaymentDTO;
import com.jbmotos.model.entity.CardPayment;
import com.jbmotos.model.repositories.CardPaymentRepository;
import com.jbmotos.services.CardPaymentService;
import com.jbmotos.services.SaleService;
import com.jbmotos.services.exception.ObjectNotFoundException;
import com.jbmotos.services.exception.BusinessRuleException;

@Service
public class CardPaymentServiceImpl implements CardPaymentService {

    private static final String PAYMENT_CARD_NOT_FOUND_MSG = "Pagamento em Cartão não encontrado para o Id informado.";

    @Autowired
    private CardPaymentRepository repository;

    @Autowired
    private SaleService saleService;

    @Autowired
    private ModelMapper mapper;


    @Transactional
    @Override
    public CardPayment saveCardPayment(CardPaymentDTO cardPaymentDTO) {
        if (this.repository.existsBySaleId(cardPaymentDTO.getSaleId())) {
            throw new BusinessRuleException("Essa Venda já possui um Pagamento em Cartão de Crédito.");
        }

        CardPayment cardPayment = this.mapper.map(cardPaymentDTO, CardPayment.class);
        var sale = this.saleService.findSaleById(cardPaymentDTO.getSaleId());
        cardPayment.setSale(sale);
        return this.repository.save(cardPayment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CardPayment> findAllCardPayments() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public CardPayment findCardPaymentById(Integer id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(PAYMENT_CARD_NOT_FOUND_MSG));
    }

    @Transactional(readOnly = true)
    @Override
    public CardPayment findCardPaymentBySaleId(Integer saleId) {
        this.saleService.validateSale(saleId);
        return this.repository.findBySaleId(saleId)
                .orElseThrow(() -> new ObjectNotFoundException("Essa venda não possui registro de pagamento" +
                        " com cartão de crédito"));
    }

    @Transactional
    @Override
    public CardPayment updateCardPayment(CardPaymentDTO cardPaymentDTO) {
        var cardPayment = findCardPaymentById(cardPaymentDTO.getId());

        this.validateSalePaymentCard(cardPayment, cardPaymentDTO);

        cardPayment.setInstallment(cardPaymentDTO.getInstallment());
        cardPayment.setFlag(cardPaymentDTO.getFlag());
        cardPayment.setInterestRate(cardPaymentDTO.getInterestRate());

        return this.repository.save(cardPayment);
    }

    @Transactional
    @Override
    public void deleteCardPayment(Integer id) {
        existsCardPaymentById(id);
        this.repository.deleteById(id);
    }

    @Override
    public void existsCardPaymentById(Integer id) {
        if (!this.repository.existsById(id)) {
            throw new ObjectNotFoundException(PAYMENT_CARD_NOT_FOUND_MSG);
        }
    }

    private void validateSalePaymentCard(CardPayment oldPayment, CardPaymentDTO newPayment) {
        if (!oldPayment.getSale().getId().equals(newPayment.getSaleId())) {
            throw new BusinessRuleException("Erro ao tentar atualizar. A Venda do Pagamento em Cartão" +
                    " não pode ser atualizada.");
        }
    }
}
