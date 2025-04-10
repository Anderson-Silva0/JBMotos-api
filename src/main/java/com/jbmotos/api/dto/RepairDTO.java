package com.jbmotos.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class RepairDTO {

    private Integer id;

    @NotBlank(message = "O campo CPF do Funcionário é obrigatório.")
    private String employeeCpf;

    private MotorcycleDTO motorcycle;

    @Valid
    private SaleDTO sale;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    @Length(min = 5, max = 300, message = "O campo Serviços Realizados deve ter entre 5 e 300 caracteres.")
    private String repairsPerformed;

    @Length(max = 300, message = "O campo Observação deve ter no máximo 300 caracteres.")
    private String observation;

    @DecimalMin(value = "0.01", inclusive = false, message = "O campo Preço de Mão de Obra deve ser maior que zero.")
    private BigDecimal laborCost;
}
