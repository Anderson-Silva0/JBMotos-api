package com.jbmotos.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jbmotos.api.validation.ValidationGroups;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.groups.ConvertGroup;
import jakarta.validation.groups.Default;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class RepairDTO {

    private Integer id;

    @Valid
    @ConvertGroup(from = Default.class, to = ValidationGroups.CpfValidationGroup.class)
    private EmployeeDTO employee;

    @Valid
    @ConvertGroup(from = Default.class, to = ValidationGroups.RepairValidationGroup.class)
    private MotorcycleDTO motorcycle;

    @Valid
    @ConvertGroup(from = Default.class, to = ValidationGroups.RepairValidationGroup.class)
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
