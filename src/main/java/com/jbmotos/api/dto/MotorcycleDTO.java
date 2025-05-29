package com.jbmotos.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jbmotos.api.validation.ValidationGroups;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.validation.groups.ConvertGroup;
import jakarta.validation.groups.Default;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class MotorcycleDTO {


    @NotNull(groups = { ValidationGroups.RepairValidationGroup.class }, message = "O campo Moto é obrigatório.")
    @DecimalMin(groups = { ValidationGroups.RepairValidationGroup.class },
            value = "0.01", inclusive = false, message = "O campo Moto é obrigatório.")
    private Integer id;

    @NotBlank(message = "O campo Placa é obrigatório.")
    @Length(min = 8, max = 8, message = "O campo Placa deve ter 8 caracteres.")
    private String plate;

    @NotBlank(message = "O campo Marca é obrigatório.")
    @Length(min = 3, max = 30, message = "O campo Marca deve ter entre 3 e 30 caracteres.")
    private String brand;

    @NotBlank(message = "O campo Modelo é obrigatório.")
    @Length(min = 3, max = 30, message = "O campo Modelo deve ter entre 3 e 30 caracteres.")
    private String model;

    @NotNull(message = "O campo Ano é obrigatório.")
    @Min(value = 1900, message = "Informe um ano maior ou igual à 1900.")
    @Max(value = 9999, message = "O campo Ano tem no máximo 4 dígitos.")
    private Integer year;

    private String motorcycleStatus;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    @Valid
    @ConvertGroup(from = Default.class, to = ValidationGroups.CpfValidationGroup.class)
    @ConvertGroup(from = ValidationGroups.RepairValidationGroup.class, to = ValidationGroups.CpfValidationGroup.class)
    private CustomerDTO customer;

}
