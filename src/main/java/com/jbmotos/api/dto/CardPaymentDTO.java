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

    @NotNull(message = "Informe o Total de Taxas.")
    @DecimalMin(value = "0.0", inclusive = false, message = "O Total de Taxas deve ser maior que zero.")
    private BigDecimal totalFees;

    @NotNull(message = "O Id da Venda não pode ser nulo.")
    private Integer saleId;
}
