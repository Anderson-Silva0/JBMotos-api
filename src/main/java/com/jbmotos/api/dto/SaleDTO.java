package com.jbmotos.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
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
public class SaleDTO {

    private Integer id;

    @Valid
    private CustomerDTO customer;

    private EmployeeDTO employee;

    @Length(groups = SaleDTO.SaleValidationGroup.class, max = 100,
            message = "O campo Observação tem no máximo 100 caracteres.")
    private String observation;

    @NotBlank(groups = SaleDTO.SaleValidationGroup.class, message = "O campo Forma de Pagamento é obrigatório.")
    @Length(groups = SaleDTO.SaleValidationGroup.class, max = 50,
            message = "O campo Forma de Pagamento tem no máximo 50 caracteres.")
    private String paymentMethod;
    
    @Valid
    private CardPaymentDTO cardPayment;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    private BigDecimal totalSaleValue;

    private List<ProductsOfSaleDTO> productsOfSale;

    public interface SaleValidationGroup {}

}
