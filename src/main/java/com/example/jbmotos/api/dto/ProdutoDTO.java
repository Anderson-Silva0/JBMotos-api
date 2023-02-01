package com.example.jbmotos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdutoDTO {

    private Integer id;

    @NotBlank(message = "O campo Nome é obrigatório")
    @Length(min = 3, max = 50, message = "O campo Nome deve ter entre 3 e 50 caracteres.")
    private String nome;

    @NotBlank(message = "O campo Preço de Venda é obrigatório")
    private BigDecimal preco_venda;

    @NotBlank(message = "O campo Estoque Mínimo é obrigatório")
    private Integer min_estoque;
}
