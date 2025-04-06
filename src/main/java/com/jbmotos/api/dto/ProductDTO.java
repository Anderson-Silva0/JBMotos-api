package com.jbmotos.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.*;

import lombok.*;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jbmotos.model.entity.ProductsOfSale;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ProductDTO {

    private Integer id;

    @NotBlank(message = "O campo Nome é obrigatório.")
    @Length(min = 3,max = 50, message = "O campo Nome deve ter entre 3 e 50 caracteres.")
    private String name;

    @NotNull(message = "O campo Preço de Custo é obrigatório.")
    @DecimalMin(value = "0.01", inclusive = false, message = "O campo Preço de Custo deve ser maior que zero.")
    private BigDecimal costPrice;

    @NotNull(message = "O campo Preço de Venda é obrigatório.")
    @DecimalMin(value = "0.01", inclusive = false, message = "O campo Preço de Venda deve ser maior que zero.")
    private BigDecimal salePrice;

    @NotBlank(message = "O campo Marca é obrigatório.")
    @Length(min = 3, max = 30, message = "O campo Marca deve ter entre 3 e 30 caracteres.")
    private String brand;

    private String productStatus;

    @NotNull(message = "O Id do Estoque não pode ser nulo.")
    private Integer stockId;

    @NotBlank(message = "O campo CNPJ é obrigatório.")
    private String supplierCnpj;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    @JsonIgnore
    private List<ProductsOfSale> productsOfSale;
}
