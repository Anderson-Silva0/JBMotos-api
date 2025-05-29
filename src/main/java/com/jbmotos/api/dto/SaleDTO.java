package com.jbmotos.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jbmotos.api.validation.ValidationGroups;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.groups.ConvertGroup;
import jakarta.validation.groups.Default;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class SaleDTO {

    private Integer id;

    @Valid
    @ConvertGroup(from = Default.class, to = ValidationGroups.CpfValidationGroup.class)
    private CustomerDTO customer;

    @Valid
    @ConvertGroup(from = Default.class, to = ValidationGroups.CpfValidationGroup.class)
    private EmployeeDTO employee;

    @Length(max = 100, message = "O campo Observação tem no máximo 100 caracteres.")
    private String observation;

    @NotBlank(groups = { Default.class, ValidationGroups.RepairValidationGroup.class},
            message = "O campo Forma de Pagamento é obrigatório.")
    @Length(groups = { Default.class, ValidationGroups.RepairValidationGroup.class},
            max = 50, message = "O campo Forma de Pagamento tem no máximo 50 caracteres.")
    private String paymentMethod;
    
    @Valid
    @ConvertGroup(from = Default.class, to = ValidationGroups.RepairValidationGroup.class)
    private CardPaymentDTO cardPayment;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    private BigDecimal totalSaleValue;

    private List<ProductsOfSaleDTO> productsOfSale;

}
