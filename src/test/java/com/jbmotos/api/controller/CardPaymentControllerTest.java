package com.jbmotos.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbmotos.api.dto.CardPaymentDTO;
import com.jbmotos.model.entity.CardPayment;
import com.jbmotos.model.entity.Sale;
import com.jbmotos.services.CardPaymentService;

@ExtendWith(MockitoExtension.class)
class CardPaymentControllerTest {

    @InjectMocks
    private CardPaymentController controller;

    @Mock
    private CardPaymentService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "mapper", new ModelMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Deve salvar pagamento por cartão")
    void save() throws Exception {
        CardPaymentDTO dto = CardPaymentDTO.builder().installment("3").flag("Visa").interestRate(new BigDecimal("2.5")).saleId(1).build();
        CardPayment payment = CardPayment.builder().id(1).installment(dto.getInstallment()).flag(dto.getFlag()).interestRate(dto.getInterestRate()).sale(Sale.builder().id(dto.getSaleId()).build()).build();

        when(service.saveCardPayment(any(CardPaymentDTO.class))).thenReturn(payment);

        mockMvc.perform(post("/api/card-payment").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag").value("Visa"));
    }

    @Test
    @DisplayName("Deve listar pagamentos por cartão")
    void findAll() throws Exception {
        when(service.findAllCardPayments()).thenReturn(List.of(CardPayment.builder().id(1).flag("Visa").installment("3").interestRate(new BigDecimal("2.5")).sale(Sale.builder().id(1).build()).build()));

        mockMvc.perform(get("/api/card-payment/find-all")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar pagamento por venda")
    void findBySaleId() throws Exception {
        CardPayment payment = CardPayment.builder().id(1).flag("Visa").installment("3").interestRate(new BigDecimal("2.5")).sale(Sale.builder().id(1).build()).build();
        when(service.findCardPaymentBySaleId(1)).thenReturn(payment);

        mockMvc.perform(get("/api/card-payment/find-by-sale-id/1")).andExpect(status().isOk()).andExpect(jsonPath("$.saleId").value(1));
    }

    @Test
    @DisplayName("Deve deletar pagamento")
    void deletePayment() throws Exception {
        doNothing().when(service).deleteCardPayment(1);

        mockMvc.perform(delete("/api/card-payment/delete/1")).andExpect(status().isNoContent());
    }
}
