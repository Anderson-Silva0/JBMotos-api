package com.jbmotos.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class CardPaymentDTO {

    private Integer id;

    @NotBlank(message = "Informe a Quantidade de Parcelas.")
    private String installment;

    @NotBlank(message = "Informe a Bandeira do Cartão.")
    private String flag;

    @NotNull(message = "Informe a Taxa de Juro.")
    @DecimalMin(value = "0.0", inclusive = false, message = "A Taxa de Juro deve ser maior que zero.")
    private BigDecimal interestRate;

    @NotNull(message = "O Id da Venda não pode ser nulo.")
    private Integer saleId;
}
