package com.jbmotos.api.dto;

import com.jbmotos.api.validation.ValidationGroups;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
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

    @NotBlank(groups = {Default.class, ValidationGroups.RepairValidationGroup.class},
            message = "Informe a Quantidade de Parcelas.")
    private String installment;

    @NotBlank(groups = {Default.class, ValidationGroups.RepairValidationGroup.class},
            message = "Informe a Bandeira do Cartão.")
    private String flag;

    @NotNull(groups = {Default.class, ValidationGroups.RepairValidationGroup.class},
            message = "Informe a Taxa de Juro.")
    @DecimalMin(groups = {Default.class, ValidationGroups.RepairValidationGroup.class},
            value = "0.0", inclusive = false, message = "A Taxa de Juro deve ser maior que zero.")
    private BigDecimal interestRate;

    @NotNull(groups = {Default.class, ValidationGroups.RepairValidationGroup.class},
            message = "O Id da Venda não pode ser nulo.")
    private Integer saleId;
}
