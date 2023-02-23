package com.example.jbmotos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdutoDTO {

    private Integer id;

    @NotBlank(message = "O campo Nome é obrigatório.")
    private String nome;

    @NotNull(message = "O campo Preço de Venda é obrigatório.")
    private BigDecimal preco_venda;

    @NotNull(message = "O campo Estoque Mínimo é obrigatório.")
    private Integer min_estoque;

    @NotBlank(message = "O campo Marca é obrigatório.")
    private String marca;
}
