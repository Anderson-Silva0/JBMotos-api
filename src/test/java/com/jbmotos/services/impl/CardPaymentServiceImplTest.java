package com.jbmotos.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jbmotos.api.dto.CardPaymentDTO;
import com.jbmotos.model.entity.CardPayment;
import com.jbmotos.model.entity.Sale;
import com.jbmotos.model.repositories.CardPaymentRepository;
import com.jbmotos.services.SaleService;
import com.jbmotos.services.exception.BusinessRuleException;
import com.jbmotos.services.exception.ObjectNotFoundException;

@ExtendWith(MockitoExtension.class)
class CardPaymentServiceImplTest {

    @InjectMocks
    private CardPaymentServiceImpl cardPaymentService;

    @Mock
    private CardPaymentRepository repository;

    @Mock
    private SaleService saleService;

    @Mock
    private ModelMapper mapper;

    private CardPaymentDTO cardPaymentDTO;
    private CardPayment cardPayment;
    private Sale sale;

    @BeforeEach
    void setUp() {
        sale = Sale.builder().id(10).build();
        cardPaymentDTO = CardPaymentDTO.builder()
                .id(1)
                .installment("12")
                .flag("Visa")
                .interestRate(new BigDecimal("1.99"))
                .saleId(10)
                .build();
        cardPayment = CardPayment.builder()
                .id(1)
                .installment("12")
                .flag("Visa")
                .interestRate(new BigDecimal("1.99"))
                .sale(sale)
                .build();
    }

    @Test
    @DisplayName("Deve salvar pagamento com cartão com sucesso")
    void saveCardPayment() {
        when(repository.existsBySaleId(cardPaymentDTO.getSaleId())).thenReturn(false);
        when(mapper.map(cardPaymentDTO, CardPayment.class)).thenReturn(cardPayment);
        when(saleService.findSaleById(cardPaymentDTO.getSaleId())).thenReturn(sale);
        when(repository.save(cardPayment)).thenReturn(cardPayment);

        CardPayment paymentSaved = cardPaymentService.saveCardPayment(cardPaymentDTO);

        assertNotNull(paymentSaved);
        assertEquals(cardPayment.getId(), paymentSaved.getId());
        assertEquals(cardPayment.getFlag(), paymentSaved.getFlag());
        verify(repository, times(1)).save(cardPayment);
    }

    @Test
    @DisplayName("Deve impedir pagamento duplicado para mesma venda")
    void saveCardPaymentAlreadyExistsForSale() {
        when(repository.existsBySaleId(cardPaymentDTO.getSaleId())).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> cardPaymentService.saveCardPayment(cardPaymentDTO));

        assertEquals("Essa Venda já possui um Pagamento em Cartão de Crédito.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve buscar todos os pagamentos em cartão")
    void findAllCardPayments() {
        when(repository.findAll()).thenReturn(List.of(cardPayment));

        List<CardPayment> result = cardPaymentService.findAllCardPayments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cardPayment, result.get(0));
    }

    @Test
    @DisplayName("Deve buscar pagamento por id")
    void findCardPaymentById() {
        when(repository.findById(cardPayment.getId())).thenReturn(Optional.of(cardPayment));

        CardPayment result = cardPaymentService.findCardPaymentById(cardPayment.getId());

        assertNotNull(result);
        assertEquals(cardPayment, result);
    }

    @Test
    @DisplayName("Deve buscar pagamento por venda")
    void findCardPaymentBySaleId() {
        doNothing().when(saleService).validateSale(sale.getId());
        when(repository.findBySaleId(sale.getId())).thenReturn(Optional.of(cardPayment));

        CardPayment result = cardPaymentService.findCardPaymentBySaleId(sale.getId());

        assertNotNull(result);
        assertEquals(cardPayment, result);
    }

    @Test
    @DisplayName("Deve atualizar pagamento em cartão com sucesso")
    void updateCardPayment() {
        CardPaymentDTO updateDto = CardPaymentDTO.builder()
                .id(1)
                .installment("6")
                .flag("Mastercard")
                .interestRate(new BigDecimal("2.5"))
                .saleId(10)
                .build();

        when(repository.findById(updateDto.getId())).thenReturn(Optional.of(cardPayment));
        when(repository.save(any(CardPayment.class))).thenReturn(cardPayment);

        CardPayment result = cardPaymentService.updateCardPayment(updateDto);

        assertNotNull(result);
        assertEquals("Mastercard", result.getFlag());
        assertEquals("6", result.getInstallment());
        verify(repository, times(1)).save(cardPayment);
    }

    @Test
    @DisplayName("Deve falhar ao atualizar pagamento de venda diferente")
    void updateCardPaymentWithDifferentSale() {
        CardPaymentDTO updateDto = CardPaymentDTO.builder()
                .id(1)
                .installment("6")
                .flag("Mastercard")
                .interestRate(new BigDecimal("2.5"))
                .saleId(999)
                .build();

        when(repository.findById(updateDto.getId())).thenReturn(Optional.of(cardPayment));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> cardPaymentService.updateCardPayment(updateDto));

        assertEquals("Erro ao tentar atualizar. A Venda do Pagamento em Cartão não pode ser atualizada.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar pagamento em cartão")
    void deleteCardPayment() {
        when(repository.existsById(cardPayment.getId())).thenReturn(true);

        cardPaymentService.deleteCardPayment(cardPayment.getId());

        verify(repository, times(1)).deleteById(cardPayment.getId());
    }

    @Test
    @DisplayName("Deve falhar ao buscar pagamento inexistente")
    void findCardPaymentByIdNotFound() {
        when(repository.findById(cardPayment.getId())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> cardPaymentService.findCardPaymentById(cardPayment.getId()));

        assertEquals("Pagamento em Cartão não encontrado para o Id informado.", exception.getMessage());
    }
}
