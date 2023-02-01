package com.example.jbmotos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CNPJ;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Fornecedor_ProdutoDTO {

    @NotBlank(message = "O campo CNPJ é obrigatório")
    @CNPJ
    private String cnpj_fornecedor;

    @NotBlank(message = "O campo Código do Produto é obrigatório")
    private Integer id_produto;

    @NotBlank(message = "O campo Quantidade do Estoque é obrigatório")
    private Integer qtd_estoque;

    @NotBlank(message = "O campo Preço do Custo é obrigatório")
    private BigDecimal preco_custo;
}
